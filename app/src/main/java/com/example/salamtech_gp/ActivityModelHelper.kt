package com.example.salamtech_gp

import android.content.Context
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.common.FileUtil

class ActivityModelHelper(context: Context) {
    private val tflite: Interpreter

    init {
        val model = FileUtil.loadMappedFile(context, "activity_model.tflite")
        val options = Interpreter.Options()
        tflite = Interpreter(model, options)
    }

    fun predictActivity(inputArray: FloatArray): String {
        val input = inputArray.reshape(1, 384)  // if you use extension function
        val output = Array(1) { FloatArray(6) }
        tflite.run(input, output)

        val maxIdx = output[0].indices.maxByOrNull { output[0][it] } ?: -1
        val labels = listOf("Walking", "Walking Upstairs", "Walking Downstairs", "Sitting", "Standing", "Laying")
        return labels[maxIdx]
    }

    private fun FloatArray.reshape(rows: Int, cols: Int): Array<FloatArray> {
        return Array(rows) { i -> FloatArray(cols) { j -> this[i * cols + j] } }
    }
}