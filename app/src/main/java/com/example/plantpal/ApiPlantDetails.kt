package com.example.plantpal

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
