package com.project.sleepwell.ui

import android.os.Bundle
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController

import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.project.sleepwell.R
import com.project.sleepwell.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment)

        navView.setupWithNavController(navController)

        if(intent.getBooleanExtra("start-tracking", false)) {
            val bundle = Bundle()
            bundle.putBoolean("start-tracking", true)
            navController.navigate(R.id.navigation_statistic
                , bundle)
        }
    }
}
