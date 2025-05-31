package com.example.plantpal.network

import com.example.plantpal.model.ApiPlant
import com.example.plantpal.model.ApiPlantResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiPlantService {
    @GET("species-list")
    suspend fun getPlants(
        @Query("key") apiKey: String,
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 20
    ): ApiPlantResponse

    @GET("species/{id}")
    suspend fun getPlantDetails(
        @Path("id") id: Int,
        @Query("key") apiKey: String
    ): ApiPlant
} 