package com.example.smartcareai.data

import com.example.smartcareai.data.models.ApiResponse
import com.example.smartcareai.data.models.LoginData
import com.example.smartcareai.data.models.LoginRequestModel
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {

    @POST("auth/login")
    suspend fun login(
        @Body request: LoginRequestModel
    ): ApiResponse<LoginData>
}