package com.project.sleepwell.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.project.sleepwell.data.repository.authRepository

class SplashModelView(private val repository: authRepository) : ViewModel() {
    fun getUser() = repository.getUser().asLiveData()
}