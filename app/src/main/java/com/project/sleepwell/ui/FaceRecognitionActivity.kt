package com.project.sleepwell.ui

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import com.project.sleepwell.R
import com.project.sleepwell.data.remote.ApiConfig
import com.project.sleepwell.data.repository.UserPreferences
import com.project.sleepwell.data.repository.userPreferencesStore
import com.project.sleepwell.databinding.ActivityFaceRecognitionBinding
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.InputStream
import androidx.exifinterface.media.ExifInterface

class FaceRecognitionActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFaceRecognitionBinding
    private lateinit var userPreferences: UserPreferences

    private var currentPhotoUri: Uri? = null // Untuk menyimpan URI file foto

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFaceRecognitionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inisialisasi UserPreferences
        userPreferences = UserPreferences.getInstance(this.userPreferencesStore)

        // Terima URI gambar dari Intent
        val imageUriString = intent.getStringExtra("imageUri")
        Log.d("FaceRecognition", "Received imageUriString: $imageUriString")
        if (imageUriString.isNullOrEmpty()) {
            Log.e("FaceRecognition", "No image URI received in Intent.")
            showErrorDialog("Error: No image was provided. Please retake the photo.")
        } else {
            currentPhotoUri = Uri.parse(imageUriString)
            Log.d("FaceRecognition", "Parsed URI: $currentPhotoUri")
            processImageFromUri(currentPhotoUri!!)
        }

        // Tombol untuk mengambil ulang foto
        binding.retakeButton.setOnClickListener {
            showDiscardDialog()
        }

        // Tombol untuk mengunggah gambar dan memprediksi
        binding.useButton.setOnClickListener {
            lifecycleScope.launch {
                val user = userPreferences.fetchUser().first()
                val token = user.token

                if (token.isEmpty()) {
                    showErrorDialog("Session expired: Please log in again to continue.")
                } else if (currentPhotoUri != null) {
                    val file = createTempFileFromUri(currentPhotoUri!!)
                    if (file != null && file.exists() && file.length() > 0) {
                        uploadImage(file, token)
                    } else {
                        showErrorDialog("Error: Unable to process the selected photo. Please try again.")
                    }
                } else {
                    showErrorDialog("Error: No photo available. Please take a photo first.")
                }
            }
        }
    }

    private fun processImageFromUri(uri: Uri) {
        try {
            Log.d("FaceRecognition", "Processing image with URI: $uri")
            val inputStream: InputStream? = contentResolver.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()

            if (bitmap != null) {
                val rotatedBitmap = rotateBitmapIfNeeded(uri, bitmap)
                binding.faceImageView.setImageBitmap(rotatedBitmap)
            } else {
                showErrorDialog("Error: Unable to decode the image. Please ensure the file is valid.")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            showErrorDialog("Error: Failed to process the image. Details: ${e.message}")
        }
    }

    private fun createTempFileFromUri(uri: Uri): File? {
        return try {
            Log.d("FaceRecognition", "Creating temp file from URI: $uri")
            val inputStream = contentResolver.openInputStream(uri)
                ?: throw Exception("Failed to open input stream from URI")
            val tempFile = File.createTempFile("temp_image", ".jpg", cacheDir)
            inputStream.use { input ->
                tempFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
            if (tempFile.length() == 0L) throw Exception("Temp file is empty")
            tempFile
        } catch (e: Exception) {
            e.printStackTrace()
            showErrorDialog("Error: Could not create temporary file. Details: ${e.message}")
            null
        }
    }

    private fun uploadImage(file: File, token: String) {
        val apiService = ApiConfig.getApiService(token)

        lifecycleScope.launch {
            try {
                Log.d("FaceRecognition", "Uploading image: ${file.absolutePath}")
                val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
                val imageBody = MultipartBody.Part.createFormData("image", file.name, requestFile)

                val response = apiService.predictImage(imageBody)

                if (response.message == "Prediction generated successfully") {
                    val prediction = response.prediction
                    val predictionLabel = if (prediction >= 0.5) "Normal" else "Drowsy"

                    AlertDialog.Builder(this@FaceRecognitionActivity)
                        .setTitle("Prediction Result")
                        .setMessage("The system detected the following condition: $predictionLabel")
                        .setPositiveButton("OK") { _, _ ->
                            navigateToMainActivity()
                        }
                        .show()
                } else {
                    showErrorDialog("Error: ${response.message}")
                }

            } catch (e: Exception) {
                e.printStackTrace()
                showErrorDialog("Error: Failed to upload the image. Details: ${e.message}")
            }
        }
    }

    private fun navigateToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("navigateTo", "HomeFragment")
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
        finish()
    }

    private fun showErrorDialog(message: String) {
        Log.e("FaceRecognition", message)
        AlertDialog.Builder(this)
            .setTitle("Error")
            .setMessage(message)
            .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun showDiscardDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_discard_scan, null)
        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(false)
            .create()

        val discardButton = dialogView.findViewById<android.widget.Button>(R.id.discard_button)
        val cancelButton = dialogView.findViewById<android.widget.Button>(R.id.cancel_button)

        discardButton.setOnClickListener {
            dialog.dismiss()
            val intent = Intent(this, FaceCameraActivity::class.java)
            startActivity(intent)
            finish()
        }

        cancelButton.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun rotateBitmapIfNeeded(uri: Uri, bitmap: Bitmap): Bitmap {
        val inputStream = contentResolver.openInputStream(uri)
        val exif = ExifInterface(inputStream!!)
        val orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)

        return when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> rotateBitmap(bitmap, 90f)
            ExifInterface.ORIENTATION_ROTATE_180 -> rotateBitmap(bitmap, 180f)
            ExifInterface.ORIENTATION_ROTATE_270 -> rotateBitmap(bitmap, 270f)
            else -> bitmap
        }
    }

    private fun rotateBitmap(bitmap: Bitmap, angle: Float): Bitmap {
        val matrix = android.graphics.Matrix()
        matrix.postRotate(angle)
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }
}
