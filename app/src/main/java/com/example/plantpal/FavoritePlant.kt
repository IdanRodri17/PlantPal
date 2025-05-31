package com.example.plantpal

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "FavoritePlantsTable")
data class FavoritePlant(
    @PrimaryKey
    val id: Int,

    @ColumnInfo(name = "common_name")
    val commonName: String?,

    @ColumnInfo(name = "scientific_name")
    val scientificName: String?,

    @ColumnInfo(name = "image_url")
    val imageUrl: String? = null
)
