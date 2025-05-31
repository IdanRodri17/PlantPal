package com.example.plantpal.repository

import android.util.Log
import com.example.plantpal.model.ApiPlant
import com.example.plantpal.model.ApiPlantDetails
import com.example.plantpal.model.ApiPlantResponse
import com.example.plantpal.network.ApiPlantService
import com.example.plantpal.util.Resource
import kotlinx.coroutines.delay
import retrofit2.HttpException
import javax.inject.Inject
import org.json.JSONObject

class PlantRepository @Inject constructor(
    private val apiService: ApiPlantService
) {
    private val apiKey = "sk-npmv683ae055bb1d910777" 

    private suspend fun <T> safeApiCall(call: suspend () -> T): Resource<T> {
        val maxRetries = 5
        val initialDelay = 2000L // 2 seconds

        for (retryAttempt in 1..maxRetries) {
            try {
                val response = call()
                return Resource.Success(response)
            } catch (e: HttpException) {
                val errorBodyString = e.response()?.errorBody()?.string()
                Log.e("PlantRepository", "HTTP Error: ${e.code()}", e)
                Log.e("PlantRepository", "Error Body: $errorBodyString")

                if (e.code() == 429) {
                    var retryAfterSeconds: Long? = null
                    try {
                        val jsonError = JSONObject(errorBodyString)
                        retryAfterSeconds = jsonError.optLong("Retry-After", -1).takeIf { it != -1L }
                    } catch (jsonException: Exception) {
                         Log.e("PlantRepository", "Failed to parse error body JSON", jsonException)
                    }

                    val errorMessage = if (retryAfterSeconds != null) {
                         "Rate limit reached. Try again in about ${retryAfterSeconds / 60} minutes." // Convert seconds to minutes
                    } else {
                         "Rate limit reached. Please try again later."
                    }

                    if (retryAttempt < maxRetries) {
                         val delayMillis = initialDelay * retryAttempt
                         Log.w("PlantRepository", "Rate limited (429). $errorMessage Retrying in $delayMillis ms. Attempt $retryAttempt/$maxRetries")
                         delay(delayMillis)
                    } else {
                         return Resource.Error(errorMessage)
                    }
                } else {
                    return Resource.Error("HTTP error: ${e.code()}" + if (!errorBodyString.isNullOrEmpty()) ": $errorBodyString" else "")
                }
            } catch (e: Exception) {
                Log.e("PlantRepository", "An error occurred", e)
                return Resource.Error(e.message ?: "An unknown error occurred")
            }
        }
        return Resource.Error("API request failed after $maxRetries retries with 429 errors.") // Should only be reached if all retries were 429
    }

    suspend fun getPlants(page: Int = 1): Resource<ApiPlantResponse> {
        Log.d("PlantRepository", "Fetching plants page $page")
        return safeApiCall { apiService.getPlants(apiKey, page) }
    }

    suspend fun getPlantDetails(id: Int): Resource<ApiPlant> {
        Log.d("PlantRepository", "Fetching plant details for id $id")
        return safeApiCall { apiService.getPlantDetails(id, apiKey) }
    }
} 