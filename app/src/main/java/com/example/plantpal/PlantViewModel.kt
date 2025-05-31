package com.example.plantpal

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.WorkInfo
import com.example.plantpal.model.ApiPlant
import com.example.plantpal.model.ApiPlantResponse
import com.example.plantpal.repository.PlantRepository
import com.example.plantpal.util.Resource
import com.example.plantpal.util.WateringReminderScheduler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlantViewModel @Inject constructor(
    private val repository: PlantRepository,
    private val wateringReminderScheduler: WateringReminderScheduler
) : ViewModel() {

    private val _plantDetails = MutableStateFlow<Resource<ApiPlant>>(Resource.Loading())
    val plantDetails: StateFlow<Resource<ApiPlant>> = _plantDetails

    private val _plants = MutableStateFlow<Resource<ApiPlantResponse>>(Resource.Loading())
    val plants: StateFlow<Resource<ApiPlantResponse>> = _plants

    val activeReminders: LiveData<List<WorkInfo>> = wateringReminderScheduler.getWateringRemindersLiveData()

    init {
        fetchPlants()
    }

    fun fetchPlants() {
        viewModelScope.launch {
            _plants.value = Resource.Loading()
            _plants.value = repository.getPlants()
        }
    }

    fun fetchPlantDetails(plantId: Int) {
        viewModelScope.launch {
            _plantDetails.value = Resource.Loading()
            _plantDetails.value = repository.getPlantDetails(plantId)
        }
    }

    fun scheduleWateringReminder(plantName: String, wateringFrequency: String) {
        wateringReminderScheduler.scheduleWateringReminder(plantName, wateringFrequency)
    }

    fun cancelWateringReminder(plantName: String) {
        wateringReminderScheduler.cancelWateringReminder(plantName)
    }
}
