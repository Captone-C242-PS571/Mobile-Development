package com.project.sleepwell.viewModel

import androidx.lifecycle.ViewModel
import com.project.sleepwell.data.repository.authRepository

class RegisterModelView(private val repository: authRepository) : ViewModel() {
    fun register(name: String, email: String, password: String) = repository.register(name, email, password)
}