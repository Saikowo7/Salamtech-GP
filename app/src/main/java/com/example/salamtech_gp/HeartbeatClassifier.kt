package com.example.salamtech_gp
import android.content.Context
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.common.FileUtil

class HeartbeatClassifier(context: Context) {

    private val interpreter: Interpreter

    init {
        val model = FileUtil.loadMappedFile(context, "heartbeat_classifier_model.tflite")
        interpreter = Interpreter(model)
    }

    fun predict(features: FloatArray): Int {
        val input = arrayOf(features) // shape: [1][5]
        val output = Array(1) { FloatArray(5) } // 4 output classes
        interpreter.run(input, output)

        return output[0].toList().indexOf(output[0].maxOrNull() ?: 0f)
    }
}
