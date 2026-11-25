package com.example.smartcareai.data.repository

import com.example.smartcareai.data.AuthApi
import com.example.smartcareai.data.models.ApiResponse
import com.example.smartcareai.data.models.LoginData
import com.example.smartcareai.data.models.LoginRequestModel

class AuthRepository {

    private val authApi: AuthApi =
        ApiClient.retrofit.create(AuthApi::class.java)

    suspend fun login(
        email: String,
        password: String
    ): ApiResponse<LoginData> {
        return authApi.login(
            LoginRequestModel(email, password)
        )
    }
}