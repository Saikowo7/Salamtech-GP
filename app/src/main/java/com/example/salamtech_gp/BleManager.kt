package com.example.salamtech_gp

import android.annotation.SuppressLint
import android.bluetooth.*
import android.bluetooth.le.*
import android.content.Context
import android.content.pm.PackageManager
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import java.util.*

class BleManager(
    private val context: Context,
    private val onMessageReceived: (String, String) -> Unit // type, message
) {
    private var pendingDescriptorWrites = mutableListOf<BluetoothGattDescriptor>()
    private var currentGatt: BluetoothGatt? = null

    private val adapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
    private val scanner: BluetoothLeScanner? = adapter?.bluetoothLeScanner
    private var lastDevice: BluetoothDevice? = null
    private val handler = Handler(Looper.getMainLooper())

    private val scanCallback = object : ScanCallback() {
        @SuppressLint("MissingPermission")
        @androidx.annotation.RequiresPermission(android.Manifest.permission.BLUETOOTH_SCAN)
        override fun onScanResult(callbackType: Int, result: ScanResult?) {
            result?.device?.let { device ->
                if (device.name == BleConstants.DEVICE_NAME) {
                    Log.d("BLEdevice", "Found ESP32 device: ${device.address}")
                    scanner?.stopScan(this)
                    lastDevice = device
                    connectToDevice(device)
                }
            }
        }
    }

    @androidx.annotation.RequiresPermission(android.Manifest.permission.BLUETOOTH_SCAN)
    fun startScan() {
        scanner?.startScan(scanCallback)
        Log.d("BLEdevice", "Started scanning...")
    }

    @SuppressLint("MissingPermission")
    private fun connectToDevice(device: BluetoothDevice) {
        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            Log.e("BLEdevice", "Missing BLUETOOTH_CONNECT permission")
            return
        }

        device.connectGatt(context, false, object : BluetoothGattCallback() {

            override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
                when (newState) {
                    BluetoothProfile.STATE_CONNECTED -> {
                        Log.d("BLEdevice", "Connected to GATT server")
                        gatt.requestMtu(247)
                        gatt.discoverServices()

                        handler.post {
                            Toast.makeText(context, "✅ Bluetooth Device Connected", Toast.LENGTH_SHORT).show()
                        }
                    }

                    BluetoothProfile.STATE_DISCONNECTED -> {
                        Log.w("BLEdevice", "Disconnected from GATT server")

                        handler.post {
                            Toast.makeText(context, "❌ Bluetooth Device Disconnected", Toast.LENGTH_SHORT).show()
                        }

                        handler.postDelayed({
                            lastDevice?.let {
                                Log.d("BLEdevice", "Attempting to reconnect...")
                                connectToDevice(it)
                            }
                        }, 3000)
                    }
                }
            }

            override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
                val service = gatt.getService(BleConstants.SERVICE_UUID)

                val bpmChar = service?.getCharacteristic(BleConstants.CHARACTERISTIC_BPM_UUID)
                val gyroChar = service?.getCharacteristic(BleConstants.CHARACTERISTIC_GYRO_UUID)

                bpmChar?.let {
                    gatt.setCharacteristicNotification(it, true)
                    val descriptor = it.getDescriptor(BleConstants.NOTIFY_DESCRIPTOR_UUID)
                    descriptor?.let { pendingDescriptorWrites.add(it) }
                }

                gyroChar?.let {
                    gatt.setCharacteristicNotification(it, true)
                    val descriptor = it.getDescriptor(BleConstants.NOTIFY_DESCRIPTOR_UUID)
                    descriptor?.let { pendingDescriptorWrites.add(it) }
                }

                writeNextDescriptor()
            }

            override fun onDescriptorWrite(gatt: BluetoothGatt, descriptor: BluetoothGattDescriptor, status: Int) {
                super.onDescriptorWrite(gatt, descriptor, status)
                writeNextDescriptor()
            }

            private fun writeNextDescriptor() {
                if (pendingDescriptorWrites.isNotEmpty()) {
                    val descriptor = pendingDescriptorWrites.removeAt(0)
                    descriptor.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                    currentGatt?.writeDescriptor(descriptor)
                }
            }

            override fun onMtuChanged(gatt: BluetoothGatt, mtu: Int, status: Int) {
                super.onMtuChanged(gatt, mtu, status)
                Log.d("BLEdevice", "✅ MTU changed to: $mtu")
            }

            override fun onCharacteristicChanged(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic) {
                val message = characteristic.getStringValue(0) ?: return
                Log.d("BLEdevice", "Received characteristic UUID: ${characteristic.uuid}")
                when (characteristic.uuid) {
                    BleConstants.CHARACTERISTIC_BPM_UUID -> {
                        Log.d("BLEdevice", "\uD83D\uDD34 BPM Data: $message")
                        onMessageReceived("bpm", message)
                    }
                    BleConstants.CHARACTERISTIC_GYRO_UUID -> {
                        Log.d("BLEdevice", "\uD83D\uDD35 GYRO Data: $message")
                        onMessageReceived("gyro", message)
                    }
                    else -> {
                        Log.w("BLEdevice", "Unknown Characteristic UUID: ${characteristic.uuid}")
                    }
                }
            }
        })
    }
}
