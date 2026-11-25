package com.example.smartcareai.data

import com.example.smartcareai.data.models.ApiResponse
import com.example.smartcareai.data.models.Hospital
import retrofit2.http.GET
import retrofit2.http.Query

data class PlacesSearchData(
    val source: String = "",
    val next_page_token: String? = null,
    val hospitals: List<Hospital> = emptyList()
)

interface HospitalApi {

    @GET("hospitals/places-search")
    suspend fun searchPlacesHospitals(
        @Query("q") query: String,
        @Query("next_page_token") nextPageToken: String? = null
    ): ApiResponse<PlacesSearchData>

    @GET("hospitals/search")
    suspend fun searchLocalHospitals(
        @Query("q") query: String
    ): ApiResponse<List<Hospital>>

    @GET("hospitals")
    suspend fun getAllHospitals(): ApiResponse<List<Hospital>>
}
