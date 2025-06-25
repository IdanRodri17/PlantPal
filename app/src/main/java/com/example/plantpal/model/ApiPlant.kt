package com.example.plantpal.model

import com.google.gson.annotations.SerializedName

data class ApiPlant(
    val id: Int,
    @SerializedName("common_name")
    val commonName: String?,
    @SerializedName("scientific_name")
    val scientificName: List<String>?,
    val watering: String?,
    val sunlight: List<String>?,
    @SerializedName("default_image")
    val defaultImage: PlantImage?
) {
    val imageUrl: String?
        get() = defaultImage?.mediumUrl
}

data class PlantImage(
    @SerializedName("medium_url")
    val mediumUrl: String?,
    @SerializedName("original_url")
    val originalUrl: String?
)
