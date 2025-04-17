package com.example.salamtech_gp

import android.annotation.SuppressLint
import android.bluetooth.*
import android.bluetooth.le.*
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import java.util.*


class BleManager(private val context: Context, private val onMessage: (String) -> Unit) {

    private val adapter = BluetoothAdapter.getDefaultAdapter()
    private val scanner = adapter.bluetoothLeScanner

    private val scanCallback = object : ScanCallback() {
        @androidx.annotation.RequiresPermission(android.Manifest.permission.BLUETOOTH_SCAN)
        override fun onScanResult(type: Int, result: ScanResult?) {
            result?.device?.let { device ->
                @SuppressLint("MissingPermission")
                if (device.name == BleConstants.DEVICE_NAME) {
                    Log.d("BLEdevice", "Found ESP32 device")
                    scanner.stopScan(this)
                    connectToDevice(device)
                }
            }
        }
    }

    @androidx.annotation.RequiresPermission(android.Manifest.permission.BLUETOOTH_SCAN)
    fun startScan() {
        scanner.startScan(scanCallback)
        Log.d("BLEdevice", "Started scanning...")
    }

    private fun connectToDevice(device: BluetoothDevice) {
        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            Log.e("BLEdevice", "Missing BLUETOOTH_CONNECT permission")
            return
        }

        device.connectGatt(context, false, object : BluetoothGattCallback() {
            @SuppressLint("MissingPermission")
            override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
                if (newState == BluetoothProfile.STATE_CONNECTED) {
                    Log.d("BLEdevice", "Connected to GATT server")
                    gatt.discoverServices()
                }
            }
            @SuppressLint("MissingPermission")
            override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
                val service = gatt.getService(BleConstants.SERVICE_UUID)
                val characteristic = service?.getCharacteristic(BleConstants.CHARACTERISTIC_UUID)
                gatt.setCharacteristicNotification(characteristic, true)

                val descriptor = characteristic?.getDescriptor(BleConstants.NOTIFY_DESCRIPTOR_UUID)
                descriptor?.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                descriptor?.let { gatt.writeDescriptor(it) }
            }

            override fun onCharacteristicChanged(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic) {
                val message = characteristic.getStringValue(0)
                Log.d("BLEdevice", "Raw message: $message")

                val bpm = message.toIntOrNull()

                if (bpm != null && bpm in 40..180) {
                    Log.d("BLEdevice", "Valid BPM received: $bpm")
                    onMessage("BPM: $bpm")  // Use this to update UI
                } else {
                    Log.w("BLEdevice", "Invalid BPM data received: $message")
                }
            }

        })
    }
}
