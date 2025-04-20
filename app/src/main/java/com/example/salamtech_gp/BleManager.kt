package com.example.salamtech_gp

import android.annotation.SuppressLint
import android.bluetooth.*
import android.bluetooth.le.*
import android.content.Context
import android.content.pm.PackageManager
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.core.app.ActivityCompat
import java.util.*

class BleManager(
    private val context: Context,
    private val onMessage: (String) -> Unit
) {

    private val adapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
    private val scanner: BluetoothLeScanner? = adapter?.bluetoothLeScanner
    private var lastDevice: BluetoothDevice? = null
    private val handler = Handler(Looper.getMainLooper())

    private val scanCallback = object : ScanCallback() {
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
                        gatt.discoverServices()
                    }
                    BluetoothProfile.STATE_DISCONNECTED -> {
                        Log.w("BLEdevice", "Disconnected from GATT server")
                        handler.postDelayed({
                            lastDevice?.let {
                                Log.d("BLEdevice", "Attempting to reconnect...")
                                connectToDevice(it)
                            }
                        }, 3000) // retry after 3 seconds
                    }
                }
            }

            override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
                val service = gatt.getService(BleConstants.SERVICE_UUID)
                val characteristic = service?.getCharacteristic(BleConstants.CHARACTERISTIC_UUID)

                if (characteristic != null) {
                    gatt.setCharacteristicNotification(characteristic, true)

                    val descriptor = characteristic.getDescriptor(BleConstants.NOTIFY_DESCRIPTOR_UUID)
                    descriptor?.let {
                        it.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                        gatt.writeDescriptor(it)
                    }
                } else {
                    Log.w("BLEdevice", "Characteristic not found in service")
                }
            }

            override fun onCharacteristicChanged(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic) {
                val message = characteristic.getStringValue(0) ?: return
                Log.d("BLEdevice", "Raw message: $message")

                val bpm = message.toIntOrNull()
                if (bpm != null && bpm in 40..180) {
                    Log.d("BLEdevice", "Valid BPM received: $bpm")
                    onMessage("BPM: $bpm")
                } else {
                    Log.w("BLEdevice", "Invalid BPM data received: $message")
                }
            }
        })
    }
}
