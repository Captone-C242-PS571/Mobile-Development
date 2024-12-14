package com.project.sleepwell.data.model

data class UserModel(
    val name: String,
    val email: String,
    val token: String,
    val isLoggedIn: Boolean = false
)