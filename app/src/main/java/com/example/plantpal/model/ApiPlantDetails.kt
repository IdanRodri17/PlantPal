package com.example.plantpal.model

data class ApiPlantDetails(
    val id: Int,
    val common_name: String?,
    val scientific_name: List<String>?,
    val watering: String?,
    val sunlight: List<String>?,
    val default_image: PlantImage?
)
