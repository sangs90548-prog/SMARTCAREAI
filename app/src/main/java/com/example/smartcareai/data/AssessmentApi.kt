package com.example.smartcareai.data

import com.example.smartcareai.data.models.ApiResponse
import com.google.gson.JsonObject
import retrofit2.http.*

data class SymptomAnalyzeRequest(
    val symptoms: String,
    val severity: String = "Moderate",
    val duration_days: Int = 2
)

data class SymptomAnalyzeResponse(
    val risk_level: String?,
    val recommendation: String?,
    val suggested_department: String?,
    val urgency: String?,
    val summary: String?
)

interface AssessmentApi {

    @POST("assessments/analyze")
    suspend fun analyzeSymptoms(
        @Header("Authorization") token: String,
        @Body request: SymptomAnalyzeRequest
    ): ApiResponse<JsonObject>

    @GET("assessments/history")
    suspend fun getAssessmentHistory(
        @Header("Authorization") token: String
    ): ApiResponse<JsonObject>
}
