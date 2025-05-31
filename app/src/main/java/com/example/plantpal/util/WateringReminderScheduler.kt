package com.example.plantpal.util

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.work.Data
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.example.plantpal.workers.WateringReminderWorker
import java.util.concurrent.TimeUnit

class WateringReminderScheduler(private val context: Context) {
    private val workManager = WorkManager.getInstance(context)
    private val _activeReminders = MutableLiveData<List<WorkInfo>>()
    val activeReminders: LiveData<List<WorkInfo>> = _activeReminders

    fun scheduleWateringReminder(plantName: String, wateringFrequency: String) {
        val workRequest = createWorkRequest(plantName, wateringFrequency)
        workManager.enqueueUniquePeriodicWork(
            "watering_reminder_$plantName",
            ExistingPeriodicWorkPolicy.UPDATE,
            workRequest
        )
        updateActiveReminders()
    }

    fun cancelWateringReminder(plantName: String) {
        workManager.cancelUniqueWork("watering_reminder_$plantName")
        updateActiveReminders()
    }

    private fun createWorkRequest(plantName: String, wateringFrequency: String): PeriodicWorkRequest {
        val intervalDays = when (wateringFrequency.lowercase()) {
            "frequent" -> 2L
            "average" -> 5L
            "minimum" -> 10L
            else -> 7L
        }

        val inputData = Data.Builder()
            .putString(WateringReminderWorker.PLANT_NAME_KEY, plantName)
            .build()

        return PeriodicWorkRequest.Builder(WateringReminderWorker::class.java, intervalDays, TimeUnit.DAYS)
            .setInputData(inputData)
            .addTag(wateringFrequency)
            .build()
    }

    private fun updateActiveReminders() {
        workManager.getWorkInfosByTagLiveData("watering_reminder")
            .observeForever { workInfos ->
                _activeReminders.value = workInfos
            }
    }
} 