package com.example.plantpal.workers

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.plantpal.R
<<<<<<< HEAD
import android.util.Log
import androidx.work.Data

=======
>>>>>>> 70b5208f6b4e5f358a23068484823b97a630b2a8

class WateringReminderWorker(
    private val context: Context,
    private val params: WorkerParameters
) : CoroutineWorker(context, params) {

    companion object {
        const val PLANT_NAME_KEY = "plant_name"
        const val CHANNEL_ID = "watering_reminder_channel"
        const val NOTIFICATION_ID = 1
<<<<<<< HEAD
        private const val TAG = "WateringReminderWorker"
    }

    override suspend fun doWork(): Result {
        val plantName = params.inputData.getString(PLANT_NAME_KEY) ?: run {
            Log.e(TAG, "Plant name not provided in input data")
            return Result.failure()
        }

        Log.d(TAG, "Starting reminder work for plant: $plantName")

        return try {
            createNotificationChannel()
            showNotification(plantName)
            Log.d(TAG, "Successfully showed notification for $plantName")

            val output = Data.Builder()
                .putString(PLANT_NAME_KEY, plantName)
                .build()

            Result.success(output)
        } catch (e: Exception) {
            Log.e(TAG, "Error showing notification for $plantName", e)
            Result.failure()
        }
    }



    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = context.getString(R.string.notification_channel_name)
            val descriptionText = context.getString(R.string.notification_channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
                enableVibration(true)
=======
    }

    override suspend fun doWork(): Result {
        val plantName = params.inputData.getString(PLANT_NAME_KEY) ?: return Result.failure()
        
        createNotificationChannel()
        showNotification(plantName)
        
        return Result.success()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Watering Reminders"
            val descriptionText = "Notifications for plant watering reminders"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
>>>>>>> 70b5208f6b4e5f358a23068484823b97a630b2a8
            }
            
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun showNotification(plantName: String) {
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
<<<<<<< HEAD
            .setSmallIcon(R.drawable.plantpal_icon_small)
            .setContentTitle(context.getString(R.string.notification_title))
            .setContentText(context.getString(R.string.notification_text, plantName))
=======
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Time to Water Your Plant!")
            .setContentText("Don't forget to water your $plantName")
>>>>>>> 70b5208f6b4e5f358a23068484823b97a630b2a8
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
<<<<<<< HEAD
        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
=======
        notificationManager.notify(NOTIFICATION_ID, notification)
>>>>>>> 70b5208f6b4e5f358a23068484823b97a630b2a8
    }
} 