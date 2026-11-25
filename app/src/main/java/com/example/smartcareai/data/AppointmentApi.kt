package com.example.smartcareai.data

import com.example.smartcareai.data.models.ApiResponse
import com.google.gson.JsonObject
import retrofit2.http.*

data class AppointmentApiModel(
    val id: String,
    val user_id: Int?,
    val hospital_id: String?,
    val doctor_id: String?,
    val appointment_type: String?,
    val date: String,
    val time_slot: String,
    val patient_name: String?,
    val patient_phone: String?,
    val accessibility_needs: String?,
    val status: String?,
    val reference_number: String?,
    val notes: String?,
    val hospital_name: String?,
    val doctor_name: String?,
    val doctor_specialization: String?,
    val department_name: String?
)

interface AppointmentApi {

    @POST("appointments")
    suspend fun bookAppointment(
        @Header("Authorization") token: String,
        @Body body: Map<String, String>
    ): ApiResponse<JsonObject>

    @GET("appointments")
    suspend fun getAppointments(
        @Header("Authorization") token: String
    ): ApiResponse<List<AppointmentApiModel>>

    @POST("appointments/{id}/cancel")
    suspend fun cancelAppointment(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): ApiResponse<JsonObject>
}
