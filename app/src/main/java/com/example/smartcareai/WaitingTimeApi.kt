package com.example.smartcareai.data.network

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

data class WaitingTimeRequest(
    val waiting_patients: Int,
    val available_doctors: Int,
    val avg_consultation_minutes: Int,
    val priority_patients: Int
)

data class WaitingTimeResponse(
    val estimated_wait_minutes: Int,
    val status: String,
    val message: String
)

interface WaitingTimeApi {

    @POST("predict-waiting-time")
    fun predictWaitingTime(
        @Body request: WaitingTimeRequest
    ): Call<WaitingTimeResponse>
}