package com.example.salamtech_gp

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.bluetooth.BluetoothAdapter
import android.graphics.Color
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.ScrollView
import android.widget.Toast
import android.widget.TextView
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet

class home_page_fragment : Fragment() {

    private lateinit var userViewModel: UserViewModel
    private lateinit var welcomeText: TextView
    private lateinit var bleManager: BleManager

    private lateinit var bpmChart: LineChart
    private lateinit var bpmDataSet: LineDataSet
    private lateinit var lineData: LineData
    private var entryIndex = 0

    @SuppressLint("MissingPermission")
    private fun initializeBluetoothAndStartScan(bpmTextView: TextView) {
        Log.d("BLEdevice", "Starting BLE")

        bleManager = BleManager(requireContext()) { message ->

            requireActivity().runOnUiThread {
                if (message.startsWith("BPM:")) {
                    val bpm = message.removePrefix("BPM:").trim().toIntOrNull()
                    if (bpm != null) {
                        val newEntry = Entry(entryIndex.toFloat(), bpm.toFloat())
                        bpmDataSet.addEntry(newEntry)
                        lineData.notifyDataChanged()
                        bpmChart.notifyDataSetChanged()
                        bpmChart.setVisibleXRangeMaximum(30f)
                        bpmChart.moveViewToX(entryIndex.toFloat())
                        entryIndex++
                    }
                }
                Log.d("BLEdevice", "Received message: $message")
                Toast.makeText(requireContext(), "BLE: $message", Toast.LENGTH_SHORT).show()
                bpmTextView.text = "$message"
            }
        }

        val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

        if (bluetoothAdapter == null) {
            Toast.makeText(requireContext(), "Bluetooth not supported on this device", Toast.LENGTH_SHORT).show()
            return
        }

        if (!bluetoothAdapter.isEnabled) {
            Toast.makeText(requireContext(), "Please enable Bluetooth", Toast.LENGTH_SHORT).show()
            return
        }

        if (PermissionUtils.hasPermissions(requireActivity())) {
            bleManager.startScan()
        } else {
            PermissionUtils.requestAllPermissions(requireActivity())
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home_page_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val bpmTextView = view.findViewById<TextView>(R.id.bpmTitleId)



        val viewModel = ViewModelProvider(requireActivity())[UserViewModel::class.java]
        val frameLayout = view.findViewById<FrameLayout>(R.id.profile_card)
        val scrollView = view.findViewById<ScrollView>(R.id.scroll_view_homepage)
        val button_setup_to_devices = view.findViewById<Button>(R.id.button_setup_to_devices)
        val imageView = view.findViewById<ImageView>(R.id.profile_picture_id)

        bpmChart = view.findViewById(R.id.bpmChart)

        bpmDataSet = LineDataSet(mutableListOf(), "BPM")
        bpmDataSet.color = Color.RED
        bpmDataSet.setDrawCircles(false)
        bpmDataSet.setDrawValues(false)
        bpmDataSet.lineWidth = 2f

        lineData = LineData(bpmDataSet)
        bpmChart.data = lineData
        bpmChart.description.isEnabled = false
        bpmChart.setTouchEnabled(false)
        bpmChart.setPinchZoom(false)

        initializeBluetoothAndStartScan(bpmTextView)

        //Loading profile image
        viewModel.fetchProfileImageUrl()
        viewModel.profileImageUrl.observe(viewLifecycleOwner) { url ->
            Glide.with(requireContext())
                .load(url)
                .circleCrop()
                .into(imageView)
        }

        //Changing welcome text to user name by retrieving it from userViewModel
        welcomeText = view.findViewById(R.id.welcomeTextView)

        userViewModel = ViewModelProvider(requireActivity())[UserViewModel::class.java]

        userViewModel.userProfile.observe(viewLifecycleOwner) { user ->
            welcomeText.text = "Welcome, ${user.fullName}"
        }

        //on button setup click move to devices fragment
        button_setup_to_devices.setOnClickListener {
            val fragment = device_page_fragment()
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.main, fragment) // Replace with your container ID
                .addToBackStack(null) // Allows back navigation
                .commit()
        }

        imageView.setOnClickListener {
            val fragment = profile_page_fragment()
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.main, fragment) // Replace with your container ID
                .addToBackStack(null) // Allows back navigation
                .commit()
        }
    }
}
