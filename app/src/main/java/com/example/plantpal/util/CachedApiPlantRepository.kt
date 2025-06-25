package com.example.plantpal.util


import android.util.Log
import com.example.plantpal.ApiPlant
import com.example.plantpal.ApiPlantService
import com.example.plantpal.model.CachedApiPlant
import com.example.plantpal.model.CachedApiPlantDao
import javax.inject.Inject

class CachedApiPlantRepository @Inject constructor(
    private val apiService: ApiPlantService,
    private val cachedDao: CachedApiPlantDao
) {
    private val apiKey = "sk-jUJ3682e15df5603a10557"

    suspend fun loadPlantsWithFallback(): Resource<List<CachedApiPlant>> {
        return try {
            val response = apiService.getPlants(apiKey)
            val apiPlants = response.data

            val cached = apiPlants.map { it.toCachedEntity() }
            Log.d("CACHE_REPO", "Saving ${cached.size} plants to local cache")

            cachedDao.clearCache()
            cachedDao.insertAll(cached)

            Resource.Success(cached)
        } catch (e: Exception) {
            Log.e("CACHE_REPO", "API failed: ${e.message}")

            val fallback = cachedDao.getAllCachedDirect()
            Log.d("CACHE_REPO", "Loaded ${fallback.size} plants from fallback cache")
            if (fallback.isNotEmpty()) Resource.Success(fallback)
            else Resource.Error("אין אינטרנט ואין צמחים זמינים")
        }
    }
    suspend fun getCachedPlants(): List<CachedApiPlant> {
        return cachedDao.getAllCachedDirect()
    }

    suspend fun saveApiPlantsToCache(apiPlants: List<ApiPlant>) {
        val cached = apiPlants.map { it.toCachedEntity() }
        cachedDao.clearCache()
        cachedDao.insertAll(cached)
    }

}
