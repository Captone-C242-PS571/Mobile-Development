package com.project.sleepwell.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.project.sleepwell.utils.alarm.AlarmInterval
import com.project.sleepwell.databinding.ActivityTrackingBinding

class TrackingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTrackingBinding

    private lateinit var intervals: List<AlarmInterval>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTrackingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnStart.setOnClickListener {
            startActivity(Intent(this, FaceCameraActivity::class.java))
        }

    }
}