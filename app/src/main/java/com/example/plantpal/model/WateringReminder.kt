package com.example.plantpal.model

data class WateringReminder(
    val plantName: String,
    val wateringFrequency: String,
    val nextWateringDate: Long
) 