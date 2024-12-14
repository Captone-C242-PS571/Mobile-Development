package com.project.sleepwell.tflite

import android.content.Context
import org.tensorflow.lite.Interpreter
import java.nio.ByteBuffer
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import java.io.FileInputStream

class TFLiteModel(private val context: Context) {

    private var interpreter: Interpreter? = null

    init {
        // Muat model TFLite
        interpreter = Interpreter(loadModelFile("model_1_fix.tflite"))
    }

    private fun loadModelFile(modelName: String): MappedByteBuffer {
        val assetFileDescriptor = context.assets.openFd(modelName)
        val inputStream = FileInputStream(assetFileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = assetFileDescriptor.startOffset
        val declaredLength = assetFileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }

    fun predict(input: FloatArray): FloatArray {
        val inputBuffer = ByteBuffer.allocateDirect(4 * input.size).apply {
            order(java.nio.ByteOrder.nativeOrder())
            for (value in input) putFloat(value)
        }

        val outputBuffer = ByteBuffer.allocateDirect(4 * input.size).apply {
            order(java.nio.ByteOrder.nativeOrder())
        }

        interpreter?.run(inputBuffer, outputBuffer)

        outputBuffer.rewind()
        val result = FloatArray(input.size)
        outputBuffer.asFloatBuffer().get(result)
        return result
    }

    fun close() {
        interpreter?.close()
    }
}