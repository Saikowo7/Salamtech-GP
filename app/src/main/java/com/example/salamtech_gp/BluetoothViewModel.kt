package com.example.salamtech_gp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class BluetoothViewModel : ViewModel() {

    // LiveData for raw BPM value
    private val _bpm = MutableLiveData<Int>()
    val bpm: LiveData<Int> get() = _bpm

    // LiveData for full feature set (Age, HR, bpm_avg, bpm_delta, delta_avg)
    private val _featureInput = MutableLiveData<FloatArray>()
    val featureInput: LiveData<FloatArray> get() = _featureInput

    // Optional: connection status
    private val _isConnected = MutableLiveData<Boolean>()
    val isConnected: LiveData<Boolean> get() = _isConnected

    // Optional: raw BLE message for debugging
    private val _rawMessage = MutableLiveData<String>()
    val rawMessage: LiveData<String> get() = _rawMessage

    // Functions to update values
    fun updateBpm(value: Int) {
        _bpm.value = value
    }

    fun updateConnectionStatus(connected: Boolean) {
        _isConnected.value = connected
    }

    fun updateRawMessage(msg: String) {
        _rawMessage.value = msg
    }

    fun updateFeatureInput(input: FloatArray) {
        // Example: [Age, HR, bpm_avg, bpm_delta, delta_avg]
        _featureInput.value = input
    }
}
