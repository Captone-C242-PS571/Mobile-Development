package com.project.sleepwell.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.project.sleepwell.R
import com.project.sleepwell.databinding.ActivitySplashBinding
import com.project.sleepwell.utils.NetworkHelper
import com.project.sleepwell.viewModel.SplashModelView
import com.project.sleepwell.viewModel.ViewModelFactory
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding
    private val viewModel by viewModels<SplashModelView> {
        ViewModelFactory.getInstance(this)
    }

    private val CAMERA_PERMISSION_REQUEST_CODE = 1001
    private val NOTIFICATION_PERMISSION_REQUEST_CODE = 1002

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Meminta izin kamera dan notifikasi
        requestPermissions()

        lifecycleScope.launch {
            delay(3000)
            if (!NetworkHelper.isConnected(this@SplashActivity)) {
                MaterialAlertDialogBuilder(this@SplashActivity)
                    .setTitle(R.string.no_internet)
                    .setMessage(R.string.no_internet_description)
                    .setPositiveButton(R.string.ok) { _, _ -> finish() }
                    .show()
            } else {
                viewModel.getUser().observe(this@SplashActivity) { user ->
                    if (!user.isLoggedIn) {
                        startActivity(Intent(this@SplashActivity, SignUpActivity::class.java))
                    } else {
                        startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                    }
                    finish()
                }
            }
        }
    }

    // Meminta izin untuk Kamera dan Notifikasi
    private fun requestPermissions() {
        // Meminta izin Kamera
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), CAMERA_PERMISSION_REQUEST_CODE)
        }

        // Meminta izin notifikasi untuk Android 13+ (POST_NOTIFICATIONS)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
                // Jika izin belum diberikan, arahkan ke pengaturan notifikasi
                val intent = Intent(android.provider.Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                intent.putExtra(android.provider.Settings.EXTRA_APP_PACKAGE, packageName)
                startActivity(intent)
            }
        }
    }

    // Menangani hasil izin yang diminta
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            CAMERA_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Camera permission granted", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show()
                }
            }
            NOTIFICATION_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Notification permission granted", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Notification permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}