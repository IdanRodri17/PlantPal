package com.example.plantpal


import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.WorkInfo
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

    var selectedFilter: Int? = null

    val activeReminders: LiveData<List<WorkInfo>> = reminderScheduler.getWateringRemindersLiveData()

    val apiKey = "sk-jUJ3682e15df5603a10557"

    init {

        fetchPlantsCacheFirst()
    }



    fun fetchPlantsCacheFirst() {
        viewModelScope.launch {
            _plantList.value = Resource.Loading()

            // 1. טען מהקאש מיד
            val cached = cachedRepository.getCachedPlants()
            if (cached.isNotEmpty()) {
                _plantList.value = Resource.Success(cached.map { it.toApiPlant() })
            }

            // 2. נסה לרענן מהאינטרנט ולעדכן גם את הקאש וגם את התצוגה
            try {
                val response = repository.getPlants(apiKey)
                val apiPlants = response.data
                _plantList.value = Resource.Success(apiPlants)
                cachedRepository.saveApiPlantsToCache(apiPlants)
            } catch (e: Exception) {
                if (cached.isEmpty()) {
                    _plantList.value = Resource.Error("אין אינטרנט ואין נתונים זמינים")
                } else {
                    Log.e("PLANT_DEBUG", "API fetch failed: ${e.message}")
                    // השאר את הקאש על המסך
                }
            }
        }
    }



/*
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
*/

    fun fetchPlants(indoor: Int? = null) {
        Log.d("FILTER_LOG", "Fetching plants with filter: indoor = $indoor")
        selectedFilter = indoor

        viewModelScope.launch {
            _plantList.value = Resource.Loading()
            try {
                val response = repository.getPlants(indoor,apiKey)
                val result = response.data

                Log.d("FILTER_LOG", "Received response: $result")
                Log.d("FILTER_LOG", "Number of plants: ${result.size}")
                Log.d("FILTER_LOG", "Received ${response.data.size} plants from API")

                _plantList.value = Resource.Success(response.data)
            } catch (e: Exception) {
                Log.e("PLANT_DEBUG", "Error filtering plants", e)
                _plantList.value = Resource.Error("Couldn't filter: ${e.message}")
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

    fun getReminderForPlant(plantName: String): LiveData<List<WorkInfo>> {
        return reminderScheduler.getWorkInfosForPlant(plantName)
    }


}
