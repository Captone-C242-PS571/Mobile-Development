package com.project.sleepwell.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.sleepwell.data.repository.authRepository
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import com.project.sleepwell.data.model.UserModel
import com.project.sleepwell.data.repository.UserPreferences

class ProfileModelView (private val repository: authRepository, private val userPreferences: UserPreferences // Pastikan userPreferences diinjeksikan di sini
) : ViewModel() {

    fun logout() {
        viewModelScope.launch {
            repository.logout()
        }
    }

    fun fetchUser(): Flow<UserModel> {
        return userPreferences.fetchUser()
    }

}