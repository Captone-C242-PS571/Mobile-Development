package com.project.sleepwell.viewModel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.project.sleepwell.data.repository.authRepository
import com.project.sleepwell.data.repository.UserPreferences
import com.project.sleepwell.utils.Injection

class ViewModelFactory(
    private val repository: authRepository,
    private val userPreferences: UserPreferences // Tambahkan parameter userPreferences
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(RegisterModelView::class.java) -> {
                RegisterModelView(repository) as T
            }

            modelClass.isAssignableFrom(LoginModelView::class.java) -> {
                LoginModelView(repository) as T
            }

            modelClass.isAssignableFrom(SplashModelView::class.java) -> {
                SplashModelView(repository) as T
            }

            modelClass.isAssignableFrom(ProfileModelView::class.java) -> {
                ProfileModelView(repository, userPreferences) as T // Injeksi userPreferences
            }

            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }

    companion object {
        @Volatile
        private var instance: ViewModelFactory? = null

        @JvmStatic
        fun getInstance(context: Context): ViewModelFactory =
            instance ?: synchronized(this) {
                instance ?: ViewModelFactory(
                    Injection.provideRepository(context),
                    Injection.provideUserPreferences(context) // Pastikan ini menginjeksi UserPreferences
                )
            }.also { instance = it }
    }
}