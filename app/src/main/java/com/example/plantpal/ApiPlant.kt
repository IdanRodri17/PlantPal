package com.example.plantpal


data class PlantResponse(
    val data: List<ApiPlant>
)

data class ApiPlant(
    val id: Int,
    val common_name: String?,
    val scientific_name: List<String>?,
    val watering: String?,
    val sunlight: List<String>?,
    val default_image: PlantImage?
) {
    val imageUrl: String?
        get() = default_image?.medium_url
}


data class PlantImage(
    val medium_url: String?
)

fun ApiPlant.toPlant(): Plant {
    return Plant(
        id = this.id,
        commonName = this.common_name ?: "Unknown",
        scientificName = this.scientific_name?.joinToString(", "),
        watering = this.watering,
        sunlight = this.sunlight?.joinToString(", "),
        imageUrl = this.imageUrl
    )
}
