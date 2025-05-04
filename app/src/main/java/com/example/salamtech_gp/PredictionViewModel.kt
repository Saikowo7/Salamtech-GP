package com.example.salamtech_gp

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
class PredictionViewModel : ViewModel() {

    private val _prediction = MutableLiveData<String>()
    val prediction: LiveData<String> get() = _prediction

    private var lastPredictionTime = 0L  // Track last prediction timestamp

    fun predictThrottled(inputRaw: FloatArray, context: Context, intervalMillis: Long = 10_000L) {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastPredictionTime < intervalMillis) return  // skip

        lastPredictionTime = currentTime  // update last call

        val means = floatArrayOf(34f, 100f, 95f, 2f, 2f)
        val stds = floatArrayOf(15f, 25f, 20f, 2f, 1.5f)

        val scaledInput = FloatArray(inputRaw.size) { i ->
            (inputRaw[i] - means[i]) / stds[i]
        }

        val classifier = HeartbeatClassifier(context)
        val resultIndex = classifier.predict(scaledInput)

        val labels = listOf("Active", "Calm", "High HR", "Low HR", "Monitoring")
        val label = labels.getOrNull(resultIndex) ?: "Unknown"

        _prediction.value = label
    }
}
