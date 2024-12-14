package com.project.sleepwell.data.repository

import androidx.lifecycle.liveData
import com.google.gson.Gson
import com.project.sleepwell.data.model.UserModel
import com.project.sleepwell.data.remote.ApiConfig
import com.project.sleepwell.data.remote.ApiServices
import com.project.sleepwell.data.remote.BaseResponse
import com.project.sleepwell.data.remote.LoginRequest
import com.project.sleepwell.data.remote.LoginResponse
import com.project.sleepwell.data.remote.SignupRequest
import kotlinx.coroutines.flow.Flow
import retrofit2.HttpException

class authRepository private constructor(
    private var Api: ApiServices,
    private val userPrefs: UserPreferences
) {
    companion object {
        @Volatile
        private var instance: authRepository? = null
        fun getInstance(
            apiService: ApiServices,
            userPreference: UserPreferences
        ) =
            instance ?: synchronized(this) {
                instance ?: authRepository(apiService, userPreference)
            }.also { instance = it }
    }

    fun register(name: String, email: String, password: String) = liveData {
        emit(ResultState.Loading)
        try {
            val signupRequest = SignupRequest(name, email, password)
            val successResponse = Api.signup(signupRequest)
            emit(ResultState.Success(successResponse))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, BaseResponse::class.java)
            emit(ResultState.Error(errorResponse.message))
        }
    }

    fun login(email: String, password: String) = liveData {
        emit(ResultState.Loading)
            try {
                val loginRequest = LoginRequest(email, password)
                val successResponse = Api.login(loginRequest)
                val newApiService = ApiConfig.getApiService(successResponse.token)
                Api = newApiService
                emit(ResultState.Success(successResponse))
            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                val errorResponse = Gson().fromJson(errorBody, LoginResponse::class.java)
                emit(ResultState.Error(errorResponse.message))
            }
    }

    suspend fun saveUser(user: UserModel) {
        userPrefs.saveUserDetails(user)
    }

    fun getUser(): Flow<UserModel> {
        return userPrefs.fetchUser()
    }

    suspend fun logout() {
        userPrefs.clearUserData()
    }

}