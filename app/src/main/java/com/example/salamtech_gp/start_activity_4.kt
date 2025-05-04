package com.example.salamtech_gp

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast

class start_activity_4 : Fragment() {

    private lateinit var bleManager: BleManager

    @SuppressLint("MissingPermission")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_start_activity_4, container, false)

        val reloadIcon = view.findViewById<ImageView>(R.id.reload_icon)

        val rotateAnimation = AnimationUtils.loadAnimation(requireContext(), R.anim.reload_rotate)
        rotateAnimation.repeatCount = Animation.INFINITE // Makes it loop continuously

        reloadIcon.startAnimation(rotateAnimation)

        //next button
        view.findViewById<Button>(R.id.button_next).setOnClickListener {
            (activity as SetupPage).loadFragment(finish_activity())

        }

        //previous button
        view.findViewById<ImageView>(R.id.back_arrow).setOnClickListener {
            (activity as SetupPage).loadFragment(start_activity_3())
        }




        // Request BLE permissions if not granted
        val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

        if (bluetoothAdapter == null) {
            Log.d("BLEdevice", "Bluetooth not supported on this device")
            Toast.makeText(requireContext(), "Bluetooth not supported on this device", Toast.LENGTH_SHORT).show()
            return view
        }

        if (!bluetoothAdapter.isEnabled) {
            Log.d("BLEdevice", "Please enable Bluetooth")
            Toast.makeText(requireContext(), "Please enable Bluetooth", Toast.LENGTH_SHORT).show()
            return view
        }

        if (PermissionUtils.hasPermissions(requireActivity())) {
            bleManager.startScan()
        } else {
            PermissionUtils.requestAllPermissions(requireActivity())
        }

        return view
    }
}