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
import androidx.fragment.app.activityViewModels
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
    private val predictionViewModel: PredictionViewModel by activityViewModels()
    private lateinit var anomalyText: TextView
    private lateinit var bpmChart: LineChart
    private lateinit var bpmDataSet: LineDataSet
    private lateinit var lineData: LineData
    private var entryIndex = 0
    private var lastKnownBpm: Float? = null
    private var lastKnownDelta: Float? = null
    private var lastKnownAvgDelta: Float? = null

    private fun parseBleData(data: String): Map<String, String> {
        return data.split(",").mapNotNull { entry ->
            val parts = entry.split(":")
            if (parts.size == 2) parts[0] to parts[1] else null
        }.toMap()
    }

    @SuppressLint("MissingPermission")
    private fun initializeBluetoothAndStartScan(bpmTextView: TextView) {
        bleManager = BleManager(requireContext()) { type, message ->
            requireActivity().runOnUiThread {
                val data = parseBleData(message)

                when (type) {
                    "bpm" -> {
                        val avgBpm = data["avg_bpm"]?.toFloatOrNull()
                        val delta = data["delta"]?.toFloatOrNull()
                        val avgDelta = data["avg_delta"]?.toFloatOrNull()

                        if (avgBpm != null && delta != null && avgDelta != null && avgBpm > 0) {
                            val input = floatArrayOf(25f, avgBpm, avgBpm, delta, avgDelta)
                            predictionViewModel.predictThrottled(input, requireContext())
                        }
                    }

                    "gyro" -> {
                        val gx = data["X"]?.toFloatOrNull()
                        val gy = data["Y"]?.toFloatOrNull()
                        val gz = data["Z"]?.toFloatOrNull()

                        if (gx != null && gy != null && gz != null) {
                            val input = floatArrayOf(gx, gy, gz)
                            //val activityLabel = ActivityModelHelper.predict(input, requireContext())
                            //Toast.makeText(requireContext(), "🚶 Activity: $activityLabel", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
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
        anomalyText = view.findViewById(R.id.AnomalyText)
        welcomeText = view.findViewById(R.id.welcomeTextView)
        val buttonSetupToDevices = view.findViewById<Button>(R.id.button_setup_to_devices)
        val imageView = view.findViewById<ImageView>(R.id.profile_picture_id)
        val frameLayout = view.findViewById<FrameLayout>(R.id.profile_card)
        val scrollView = view.findViewById<ScrollView>(R.id.scroll_view_homepage)

        bpmChart = view.findViewById(R.id.bpmChart)
        bpmDataSet = LineDataSet(mutableListOf(), "BPM").apply {
            color = Color.RED
            setDrawCircles(false)
            setDrawValues(false)
            lineWidth = 2f
        }

        lineData = LineData(bpmDataSet)
        bpmChart.data = lineData
        bpmChart.description.isEnabled = false
        bpmChart.setTouchEnabled(false)
        bpmChart.setPinchZoom(false)

        initializeBluetoothAndStartScan(bpmTextView)

        // ✅ Observe prediction once — outside the scan
        predictionViewModel.prediction.observe(viewLifecycleOwner) { result ->
            anomalyText.text = "Prediction: $result"
        }

        // Profile image loading
        userViewModel = ViewModelProvider(requireActivity())[UserViewModel::class.java]
        userViewModel.fetchProfileImageUrl()
        userViewModel.profileImageUrl.observe(viewLifecycleOwner) { url ->
            Glide.with(requireContext())
                .load(url)
                .circleCrop()
                .into(imageView)
        }

        // Welcome text
        userViewModel.userProfile.observe(viewLifecycleOwner) { user ->
            welcomeText.text = "Welcome, ${user.fullName}"
        }

        // Navigation
        buttonSetupToDevices.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.main, device_page_fragment())
                .addToBackStack(null)
                .commit()
        }

        imageView.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.main, profile_page_fragment())
                .addToBackStack(null)
                .commit()
        }
    }
}