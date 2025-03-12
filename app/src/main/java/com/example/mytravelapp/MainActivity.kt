package com.example.mytravelapp

import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.io.IOException
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

class MainActivity : AppCompatActivity() {

    private var currentImage = 0
    private lateinit var image: ImageView
    private lateinit var placeName: TextView
    private lateinit var interpreter: Interpreter

    private val places = arrayOf("CHARMINAR", "MUSEUM", "TANK BUND", "GOLCONDA FORT", "HYDERABAD PUBLIC SCHOOL")
    private val images = arrayOf(
        R.drawable.pic1,  // Ensure these resources exist in res/drawable/
        R.drawable.pic2,
        R.drawable.pic3,
        R.drawable.pic4,
        R.drawable.pic5
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)  // Ensure activity_main.xml exists in res/layout/

        val next = findViewById<ImageButton>(R.id.btnNext)
        val prev = findViewById<ImageButton>(R.id.btnPrev)
        placeName = findViewById(R.id.tVName)
        image = findViewById(R.id.imageView)

        // Initialize UI
        updateUI()

        // ✅ Initialize TensorFlow Lite model
        try {
            interpreter = Interpreter(loadModelFile("mobilenet_v2.tflite"))
            Log.d("TFLite", "Model loaded successfully!")
        } catch (e: IOException) {
            e.printStackTrace()
            Log.e("TFLite", "Error loading model: ${e.message}")
        }

        // ✅ Log memory usage
        val runtime = Runtime.getRuntime()
        val usedMemInMB = (runtime.totalMemory() - runtime.freeMemory()) / 1048576L
        val maxHeapSizeInMB = runtime.maxMemory() / 1048576L
        Log.d("Memory", "Used Memory: $usedMemInMB MB, Max Heap: $maxHeapSizeInMB MB")

        // ✅ Handle button clicks
        next.setOnClickListener {
            currentImage = (currentImage + 1) % images.size
            updateUI()
        }

        prev.setOnClickListener {
            currentImage = (currentImage - 1 + images.size) % images.size
            updateUI()
        }
    }

    private fun updateUI() {
        image.setImageResource(images[currentImage])
        placeName.text = places[currentImage]
    }

    // ✅ Load TensorFlow Lite model from assets
    private fun loadModelFile(modelPath: String): MappedByteBuffer {
        val assetFileDescriptor = assets.openFd(modelPath)
        val fileInputStream = FileInputStream(assetFileDescriptor.fileDescriptor)
        val fileChannel = fileInputStream.channel
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, assetFileDescriptor.startOffset, assetFileDescriptor.declaredLength)
    }
}
