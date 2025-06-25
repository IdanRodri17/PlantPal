package com.example.plantpal

<<<<<<< HEAD
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
@Parcelize
data class ApiPlantDetails(
    @SerializedName("id") val id: Int?,
    @SerializedName("common_name") val commonName: String?,
    @SerializedName("scientific_name") val scientificName: List<String>?,
    @SerializedName("watering") val watering: String?,
    @SerializedName("sunlight") val sunlight: List<String>?,
    @SerializedName("default_image") val defaultImage: DefaultImage?
) : Parcelable


@Parcelize
data class WateringBenchmark(
    @SerializedName("value") val value: String?,
    @SerializedName("unit") val unit: String?
) : Parcelable

@Parcelize
data class DefaultImage(
    @SerializedName("original_url") val original_url: String?
) : Parcelable
=======
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
>>>>>>> 70b5208f6b4e5f358a23068484823b97a630b2a8
