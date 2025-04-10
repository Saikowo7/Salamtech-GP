package com.example.salamtech_gp

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat

object PermissionUtils {
    val permissions = arrayOf(
        Manifest.permission.BLUETOOTH_SCAN,
        Manifest.permission.BLUETOOTH_CONNECT,
        Manifest.permission.ACCESS_FINE_LOCATION
    )

    fun hasPermissions(activity: Activity): Boolean {
        return permissions.all {
            ActivityCompat.checkSelfPermission(activity, it) == PackageManager.PERMISSION_GRANTED
        }
    }

    fun requestAllPermissions(activity: Activity) {
        ActivityCompat.requestPermissions(activity, permissions, 1)
    }
}