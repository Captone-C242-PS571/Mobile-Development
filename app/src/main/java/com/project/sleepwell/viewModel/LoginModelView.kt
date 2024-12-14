package com.project.sleepwell.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.sleepwell.data.model.UserModel
import com.project.sleepwell.data.repository.authRepository
import kotlinx.coroutines.launch

class LoginModelView(private val repository: authRepository) : ViewModel() {

    fun login(email: String, password: String) = repository.login(email, password)

    fun saveUser(user: UserModel) {
        viewModelScope.launch {
            repository.saveUser(user)
        }
    }
}