package com.example.plantpal

import android.os.Parcelable
import com.example.plantpal.model.CachedApiPlant
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

data class PlantResponse(
    val data: List<ApiPlant>
)

@Parcelize
data class ApiPlant(
    @SerializedName("id") val id: Int,
    @SerializedName("common_name") val commonName: String?,
    @SerializedName("scientific_name") val scientificName: List<String>?,
    @SerializedName("watering") val watering: String?,
    @SerializedName("sunlight") val sunlight: List<String>?,
    @SerializedName("default_image") val defaultImage: PlantImage?
) : Parcelable {
    val imageUrl: String?
        get() = defaultImage?.originalUrl
}

@Parcelize
data class PlantImage(
    @SerializedName("original_url") val originalUrl: String?,
    @SerializedName("medium_url") val mediumUrl: String?
) : Parcelable


fun ApiPlant.toPlant(): Plant {
    return Plant(
        id = this.id,
        commonName = this.commonName ?: "Unknown",
        scientificName = this.scientificName?.joinToString(", "),
        watering = this.watering,
        sunlight = this.sunlight?.joinToString(", "),  // ⬅️ המרה ממערך למחרוזת אחת
        imageUrl = this.imageUrl
    )
}



