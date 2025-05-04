package com.example.salamtech_gp

import java.util.*

object BleConstants {
    const val DEVICE_NAME = "ESP32_HeartMonitor"

    val SERVICE_UUID: UUID = UUID.fromString("6e400001-b5a3-f393-e0a9-e50e24dcca9e")

    // BPM characteristic
    val CHARACTERISTIC_BPM_UUID: UUID = UUID.fromString("6e400003-b5a3-f393-e0a9-e50e24dcca9e")

    // Gyroscope characteristic
    val CHARACTERISTIC_GYRO_UUID: UUID = UUID.fromString("6e400004-b5a3-f393-e0a9-e50e24dcca9e")

    // Descriptor for notifications
    val NOTIFY_DESCRIPTOR_UUID: UUID = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")
}