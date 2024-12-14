package com.project.sleepwell.data.remote

import com.google.gson.annotations.SerializedName

data class BaseResponse(
    @field:SerializedName("message")
    val message: String
)

data class LoginResponse(
    @field:SerializedName("message")
    val message: String,

    @field:SerializedName("token")
    val token: String,

    @field:SerializedName("data")
    val data: Data,
)

data class Data(

    @field:SerializedName("uid")
    val uid: String,

    @field:SerializedName("name")
    val name: String,

    @field:SerializedName("email")
    val email: String
)

data class LoginRequest(
    val email: String,
    val password: String,
)

data class SignupRequest(
    val name: String,
    val email: String,
    val password: String
)

data class PredictionResponse(
    @field:SerializedName("message")
    val message: String,

    @field:SerializedName("prediction")
    val prediction: Float,

    @field:SerializedName("imageUrl")
    val imageUrl: String
)

