package com.project.sleepwell.data.remote

import okhttp3.MultipartBody
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part


interface ApiServices {
    @POST("auth/signup")
    suspend fun signup(
        @Body signupRequest: SignupRequest
    ): BaseResponse

    @POST("auth/login")
    suspend fun login(
        @Body loginRequest: LoginRequest
    ): LoginResponse

    @POST("predict/image")
    @Multipart
    suspend fun predictImage(
        @Part image: MultipartBody.Part
    ): PredictionResponse

}