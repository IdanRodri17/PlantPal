package com.example.plantpal

import javax.inject.Inject

class PlantRepository @Inject constructor(
    private val apiService: ApiPlantService
) {
    suspend fun getPlants(apiKey: String): PlantResponse {
        return apiService.getPlants(apiKey)
    }

    suspend fun getPlantDetails(id: Int , apiKey: String): ApiPlantDetails  {
        return apiService.getPlantDetails(id, apiKey)
    }

}
