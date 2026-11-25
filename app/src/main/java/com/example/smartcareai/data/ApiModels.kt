package com.example.smartcareai.data.models

import com.google.gson.JsonObject

data class ApiResponse<T>(
    val success: Boolean,
    val message: String,
    val data: T?
)

data class LoginData(
    val user: JsonObject?,
    val token: String
)

data class LoginRequestModel(
    val email: String,
    val password: String
)
