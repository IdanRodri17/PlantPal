package com.example.plantpal.util

import com.example.plantpal.ApiPlant
import com.example.plantpal.PlantImage
import com.example.plantpal.model.CachedApiPlant

fun ApiPlant.toCachedEntity(): CachedApiPlant {
    return CachedApiPlant(
        id = this.id,
        commonName = this.commonName ?: "",
        watering = this.watering?: "",
        sunlight = (this.sunlight?: "").toString(),
        scientificName = this.scientificName?.joinToString(", ") ?: "",
        imageUrl = this.imageUrl?: ""
    )
}

fun CachedApiPlant.toApiPlant(): ApiPlant {
    return ApiPlant(
        id = this.id,
        commonName = this.commonName,
        scientificName = this.scientificName?.split(", "),
        watering = null,
        sunlight = null,
        defaultImage = PlantImage(
            originalUrl = this.imageUrl,
            mediumUrl = this.imageUrl)
    )
}
