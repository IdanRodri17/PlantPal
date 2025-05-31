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

    fun scheduleWateringReminder(plantName: String, wateringFrequency: String) {
        android.util.Log.d("REMINDER_DEBUG", "Scheduling reminder for $plantName with frequency $wateringFrequency.")
        val workRequest = createWorkRequest(plantName, wateringFrequency)
        workManager.enqueueUniquePeriodicWork(
            "watering_reminder_$plantName",
            ExistingPeriodicWorkPolicy.UPDATE,
            workRequest
        )
    }

    fun cancelWateringReminder(plantName: String) {
        android.util.Log.d("REMINDER_DEBUG", "Cancelling reminder for $plantName.")
        workManager.cancelUniqueWork("watering_reminder_$plantName")
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
            .addTag("watering_reminder")
            .addTag(plantName)
            .addTag(wateringFrequency)
            .build()
    }

    fun getWateringRemindersLiveData(): LiveData<List<WorkInfo>> {
        return workManager.getWorkInfosByTagLiveData("watering_reminder")
    }
} 