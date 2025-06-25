package com.example.plantpal

import javax.inject.Inject

class PlantRepository @Inject constructor(
    private val apiService: ApiPlantService
) {
    suspend fun getPlants(apiKey: String): PlantResponse {
        return apiService.getPlants(apiKey)
    }

<<<<<<< HEAD
    suspend fun getPlants(indoor: Int? ,apiKey: String): PlantResponse {
        return apiService.getPlants(indoor,apiKey)
    }

=======
>>>>>>> 70b5208f6b4e5f358a23068484823b97a630b2a8
    suspend fun getPlantDetails(id: Int , apiKey: String): ApiPlantDetails  {
        return apiService.getPlantDetails(id, apiKey)
    }

}
