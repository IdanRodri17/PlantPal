package com.example.plantpal

<<<<<<< HEAD
import android.os.Parcelable
import com.example.plantpal.model.CachedApiPlant
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
=======
>>>>>>> 70b5208f6b4e5f358a23068484823b97a630b2a8

data class PlantResponse(
    val data: List<ApiPlant>
)

<<<<<<< HEAD
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

=======
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
>>>>>>> 70b5208f6b4e5f358a23068484823b97a630b2a8

fun ApiPlant.toPlant(): Plant {
    return Plant(
        id = this.id,
<<<<<<< HEAD
        commonName = this.commonName ?: "Unknown",
        scientificName = this.scientificName?.joinToString(", "),
        watering = this.watering,
        sunlight = this.sunlight?.joinToString(", "),  // ⬅️ המרה ממערך למחרוזת אחת
        imageUrl = this.imageUrl
    )
}



=======
        commonName = this.common_name ?: "Unknown",
        scientificName = this.scientific_name?.joinToString(", "),
        watering = this.watering,
        sunlight = this.sunlight?.joinToString(", "),
        imageUrl = this.imageUrl
    )
}
>>>>>>> 70b5208f6b4e5f358a23068484823b97a630b2a8
