package com.example.plantpal.repository

import android.util.Log
import com.example.plantpal.model.ApiPlant
import com.example.plantpal.model.ApiPlantDetails
import com.example.plantpal.model.ApiPlantResponse
import com.example.plantpal.network.ApiPlantService
import com.example.plantpal.util.Resource
import javax.inject.Inject

class PlantRepository @Inject constructor(
    private val apiService: ApiPlantService
) {
    private val apiKey = "sk-jUJ3682e15df5603a10557" // Replace with your actual API key

    suspend fun getPlants(page: Int = 1): Resource<ApiPlantResponse> {
        return try {
            Log.d("PlantRepository", "Fetching plants page $page")
            val response = apiService.getPlants(apiKey, page)
            Log.d("PlantRepository", "Received ${response.data.size} plants")
            Resource.Success(response)
        } catch (e: Exception) {
            Log.e("PlantRepository", "Error fetching plants", e)
            Resource.Error(e.message ?: "An error occurred")
        }
    }

    suspend fun getPlantDetails(id: Int): Resource<ApiPlant> {
        return try {
            Log.d("PlantRepository", "Fetching plant details for id $id")
            val response = apiService.getPlantDetails(id, apiKey)
            Log.d("PlantRepository", "Received plant details for ${response.commonName}")
            Resource.Success(response)
        } catch (e: Exception) {
            Log.e("PlantRepository", "Error fetching plant details", e)
            Resource.Error(e.message ?: "An error occurred")
        }
    }
} 