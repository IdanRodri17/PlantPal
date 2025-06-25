package com.example.plantpal.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "CachedApiPlantsTable")
data class CachedApiPlant(
    @PrimaryKey val id: Int,
    val commonName: String?,
    val scientificName: String?,
    val watering: String?,
    val sunlight: String?,
    val imageUrl: String?
)
