package com.example.plantpal

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
        watering = null,
        sunlight = null,
        imageUrl = this.imageUrl
    )

    private fun Plant.toFavorite() = FavoritePlant(
        id = this.id,
        commonName = this.commonName,
        scientificName = this.scientificName,
        imageUrl = this.imageUrl
    )



}
