package com.example.plantpal

import com.example.plantpal.util.Resource
import javax.inject.Inject

class FavoriteRepository @Inject constructor(
    private val dao: FavoritePlantDao,
    private val apiService: ApiPlantService
) {

    fun getFavorites() = dao.getAllFavorites()

    suspend fun add(plant: FavoritePlant) {
        dao.insert(plant)
    }

    suspend fun update(plant: FavoritePlant) {
        dao.update(plant)
    }

    suspend fun remove(plant: FavoritePlant) {
        dao.deleteById(plant.id)
    }

    suspend fun isFavorite(plantId: Int): Boolean {
        return dao.isFavorite(plantId)
    }

    suspend fun addPlantToFavorites(plant: Plant) {
        val favorite = FavoritePlant(
            id = plant.id,
            commonName = plant.commonName,
            scientificName = plant.scientificName,
            imageUrl = plant.imageUrl,
            watering = plant.watering,
            sunlight = plant.sunlight
        )
        dao.insert(favorite)
    }

    suspend fun removePlantFromFavorites(plant: Plant) {
        dao.deleteById(plant.id)
    }

    suspend fun removeById(plantId: Int) = dao.deleteById(plantId)

    suspend fun getPlantDetails(plantId: Int): ApiPlant = apiService.getPlantDetails(plantId)


    }



