package com.example.plantpal

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query


interface ApiPlantService {
    @GET("species-list")
    suspend fun getPlants(
        @Query("key") apiKey: String
    ): PlantResponse

    @GET("species-list")
    suspend fun getPlants(
        @Query("indoor") indoor: Int? = null,
        @Query("key") apiKey: String

    ): PlantResponse

    @GET("plants/{id}")
    suspend fun getPlantDetails(@Path("id") plantId: Int): ApiPlant

    @GET("species/details/{id}")
    suspend fun getPlantDetails(
        @Path("id") id: Int,
        @Query("key") apiKey: String
    ): ApiPlantDetails
}
