package com.example.mytravelapp

import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.spotify.annoy.AnnoyIndex
import org.tensorflow.lite.Interpreter
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

class MainActivity : AppCompatActivity() {

    private var currentImage = 0
    private lateinit var image: ImageView
    private lateinit var placeName: TextView
    private lateinit var interpreter: Interpreter
    private lateinit var annoyIndex: AnnoyIndex

    private val places = arrayOf("CHARMINAR", "MUSEUM", "TANK BUND", "GOLCONDA FORT", "HYDERABAD PUBLIC SCHOOL")
    private val images = arrayOf(
        R.drawable.pic1,
        R.drawable.pic2,
        R.drawable.pic3,
        R.drawable.pic4,
        R.drawable.pic5
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val next = findViewById<ImageButton>(R.id.btnNext)
        val prev = findViewById<ImageButton>(R.id.btnPrev)
        placeName = findViewById(R.id.tVName)
        image = findViewById(R.id.imageView)

        updateUI()

        try {
            interpreter = Interpreter(loadModelFile("mobilenet_v2.tflite"))
            Log.d("TFLite", "Model loaded successfully!")
        } catch (e: IOException) {
            Log.e("TFLite", "Error loading model: ${e.message}")
        }

        try {
            val annoyFile = loadAnnoyFile("annoy_index.ann")
            annoyIndex = AnnoyIndex(128)
            annoyIndex.load(annoyFile.absolutePath)
            Log.d("Annoy", "Annoy index loaded successfully!")
        } catch (e: Exception) {
            Log.e("Annoy", "Error loading Annoy index: ${e.message}")
        }

        val runtime = Runtime.getRuntime()
        val usedMemInMB = (runtime.totalMemory() - runtime.freeMemory()) / 1048576L
        val maxHeapSizeInMB = runtime.maxMemory() / 1048576L
        Log.d("Memory", "Used Memory: $usedMemInMB MB, Max Heap: $maxHeapSizeInMB MB")

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

    private fun loadModelFile(modelPath: String): MappedByteBuffer {
        val assetFileDescriptor = assets.openFd(modelPath)
        val fileInputStream = FileInputStream(assetFileDescriptor.fileDescriptor)
        val fileChannel = fileInputStream.channel
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, assetFileDescriptor.startOffset, assetFileDescriptor.declaredLength)
    }

    private fun loadAnnoyFile(filename: String): File {
        val file = File(filesDir, filename)
        if (!file.exists()) {
            assets.open(filename).use { inputStream ->
                FileOutputStream(file).use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
        }
        return file
    }
}
