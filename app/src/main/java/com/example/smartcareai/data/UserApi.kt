package com.example.smartcareai.data

import com.example.smartcareai.data.models.ApiResponse
import com.google.gson.JsonObject
import retrofit2.http.*

interface UserApi {

    @GET("user/profile")
    suspend fun getProfile(
        @Header("Authorization") token: String
    ): ApiResponse<JsonObject>

    @POST("user/profile")
    suspend fun updateProfile(
        @Header("Authorization") token: String,
        @Body body: Map<String, Any>
    ): ApiResponse<JsonObject>
}
