package com.example.plantpal

data class WateringReminder(
    val plantName: String,
    val wateringFrequency: String,
    val nextWateringDate: Long
) 