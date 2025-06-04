package com.example.plantpal


import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.plantpal.model.CachedApiPlant
import com.example.plantpal.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel

import com.example.plantpal.util.CachedApiPlantRepository
import com.example.plantpal.util.WateringReminderScheduler
import com.example.plantpal.util.toApiPlant


import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlantViewModel @Inject constructor(
    private val repository: PlantRepository,
    private val cachedRepository: CachedApiPlantRepository,
    private val reminderScheduler: WateringReminderScheduler
) : ViewModel() {


    private val _plantList = MutableLiveData<Resource<List<ApiPlant>>>()
    val plantList: LiveData<Resource<List<ApiPlant>>> = _plantList

    private val _cachedPlants = MutableLiveData<Resource<List<CachedApiPlant>>>()
    val cachedPlants: LiveData<Resource<List<CachedApiPlant>>> = _cachedPlants

    private val _plantDetails = MutableLiveData<Resource<ApiPlantDetails>>()
    val plantDetails: LiveData<Resource<ApiPlantDetails>> = _plantDetails

    val apiKey = "sk-jUJ3682e15df5603a10557"

    val activeReminders = reminderScheduler.getWateringRemindersLiveData()

    init {
        fetchPlantsWithFallback()
    }


    fun fetchPlantsWithFallback() {
        viewModelScope.launch {
            _plantList.value = Resource.Loading()
            val result = cachedRepository.loadPlantsWithFallback()
            when (result) {
                is Resource.Success -> _plantList.value = Resource.Success(result.data.map { it.toApiPlant() })
                is Resource.Error -> _plantList.value = Resource.Error(result.message)
                else -> Unit
            }
        }
    }



    fun fetchPlantsFromApi() {
        viewModelScope.launch {
            _plantList.value = Resource.Loading() // ⏳ טוען
            try {
                val response = repository.getPlants(apiKey)
                _plantList.value = Resource.Success(response.data)
            } catch (e: Exception) {
                Log.e("PLANT_DEBUG", "Error fetching plants", e)
                _plantList.value = Resource.Error("Failed to load plants: ${e.message}")
            }
        }
    }


    fun fetchPlantDetails(id: Int) {
        viewModelScope.launch {
            _plantDetails.value = Resource.Loading() // ⏳

            try {
                val details = repository.getPlantDetails(id, apiKey)
                _plantDetails.value = Resource.Success(details) // ✅
            } catch (e: Exception) {
                Log.e("PLANT_DEBUG", "Error fetching details", e)
                _plantDetails.value = Resource.Error("Failed to load: ${e.message}")
            }
        }
    }

    fun scheduleWateringReminder(plantName: String, wateringFrequency: String) {
        reminderScheduler.scheduleWateringReminder(plantName, wateringFrequency)
    }

    fun cancelWateringReminder(plantName: String) {
        reminderScheduler.cancelWateringReminder(plantName)
    }

    fun scheduleTestReminder(plantName: String) {
        reminderScheduler.scheduleTestReminder(plantName)
    }

}
