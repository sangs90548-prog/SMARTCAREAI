package com.example.smartcareai.data.repository

import com.example.smartcareai.data.AppointmentApi
import com.example.smartcareai.data.AssessmentApi
import com.example.smartcareai.data.HospitalApi
import com.example.smartcareai.data.UserApi
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {

    private const val BASE_URL =
        "http://172.23.176.197/SmartCareAI/backend/public/index.php/"

    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val appointmentApi: AppointmentApi by lazy {
        retrofit.create(AppointmentApi::class.java)
    }

    val assessmentApi: AssessmentApi by lazy {
        retrofit.create(AssessmentApi::class.java)
    }

    val userApi: UserApi by lazy {
        retrofit.create(UserApi::class.java)
    }

    val hospitalApi: HospitalApi by lazy {
        retrofit.create(HospitalApi::class.java)
    }
}
