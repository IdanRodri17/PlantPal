package com.example.plantpal

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializer

data class ApiPlantDetails(
    val id: Int,
    val common_name: String?,
    val scientific_name: List<String>?,
    val watering: String?,
    val sunlight: List<String>?,
    val default_image: DefaultImage?
)

data class DefaultImage(
    val original_url: String?
)
