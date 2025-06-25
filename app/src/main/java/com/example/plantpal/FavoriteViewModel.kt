package com.example.plantpal

import android.app.Dialog
import android.util.Log
import android.widget.ImageView
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bumptech.glide.Glide
import com.example.plantpal.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class FavoriteViewModel @Inject constructor(
    private val repository: FavoriteRepository
) : ViewModel() {

    val favorites: LiveData<List<FavoritePlant>> = repository.getFavorites()

//    fun toggleFavorite(plant: Plant) {
//        Log.d("FAV_DEBUG", "toggleFavorite called for id: ${plant.id}")
//        viewModelScope.launch(Dispatchers.IO) {
//            if (repository.isFavorite(plant.id)) {
//                Log.d("FAV_DEBUG", "Plant is favorite. Removing.")
//                repository.remove(plant.toFavorite())
//            } else {
//                try {
//                    Log.d("FAV_DEBUG", "Fetching details for id: ${plant.id}")
//                    val apiPlant = repository.getPlantDetails(plant.id)
//                    val fullPlant = apiPlant.toPlant()
//                    Log.d("FAV_DEBUG", "Fetched API plant id: ${apiPlant.id}, name: ${apiPlant.commonName}, watering: ${apiPlant.watering}, sunlight: ${apiPlant.sunlight}")
//                    Log.d("FAV_DEBUG", "Adding plant to favorites: ${plant.id}")
//                    repository.add(fullPlant.toFavorite())
//                    Log.d("FAV_DEBUG", "Plant added")
//
//                } catch (e: Exception) {
//                    Log.e("FavoriteViewModel", "Error fetching details: ${e.message}")
//                }
//            }
//        }
//    }
//


    fun toggleFavorite(plant: Plant) {
        viewModelScope.launch(Dispatchers.IO) {
            val favorite = plant.toFavorite()
            if (repository.isFavorite(plant.id)) {
                repository.remove(favorite)
            } else {
                repository.add(favorite)
            }
        }
    }

    fun isFavorite(plantId: Int): Boolean {
        return favorites.value?.any { it.id == plantId } == true
    }

    fun addFavorite(plant: Plant) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.add(plant.toFavorite())
        }
    }

    fun updateFavorite(plant: Plant) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.update(plant.toFavorite())
        }
    }

    fun deleteFavorite(plant: Plant) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.remove(plant.toFavorite())
        }
    }

    fun FavoritePlant.toPlant() = Plant(
        id = this.id,
        commonName = this.commonName,
        scientificName = this.scientificName,
        watering = this.watering,
        sunlight = this.sunlight,
        imageUrl = this.imageUrl
    )

    private fun Plant.toFavorite() = FavoritePlant(
        id = this.id,
        commonName = this.commonName,
        scientificName = this.scientificName,
        imageUrl = this.imageUrl,
        watering = this.watering,
        sunlight = this.sunlight
    )






//    fun addFavoriteWithDetails(plantId: Int) {
//        viewModelScope.launch(Dispatchers.IO) {
//            try {
//                val details = repository.getPlantDetails(plantId) // קריאה ל־API
//                val plant = details.toPlant() // פונקציה שתמיר ל-Plant עם כל הנתונים
//                repository.add(plant.toFavorite())
//            } catch (e: Exception) {
//                Log.e("FavoriteViewModel", "Error adding favorite with details", e)
//            }
//        }
//    }


}
