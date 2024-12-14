package com.project.sleepwell.utils

import android.content.Context
import com.project.sleepwell.data.remote.ApiConfig
import com.project.sleepwell.data.repository.UserPreferences
import com.project.sleepwell.data.repository.authRepository
import com.project.sleepwell.data.repository.userPreferencesStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

object Injection {

    // Menyediakan instance authRepository
    fun provideRepository(context: Context): authRepository {
        val pref = UserPreferences.getInstance(context.userPreferencesStore) // Menggunakan context.userPreferencesStore
        val user = runBlocking { pref.fetchUser().first() }
        val apiService = ApiConfig.getApiService(user.token)
        return authRepository.getInstance(apiService, pref)
    }

    // Menyediakan instance UserPreferences
    fun provideUserPreferences(context: Context): UserPreferences {
        return UserPreferences.getInstance(context.userPreferencesStore) // Menggunakan context.userPreferencesStore
    }
}
