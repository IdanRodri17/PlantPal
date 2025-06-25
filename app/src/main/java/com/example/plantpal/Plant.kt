package com.example.plantpal

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize


@Parcelize
@Entity(tableName = "PlantsTable")
data class Plant(
    @PrimaryKey(autoGenerate = true)
    val id: Int,

    @ColumnInfo(name = "common_name")
    val commonName: String?,

    @ColumnInfo(name = "scientific_name")
    val scientificName: String?,

    @ColumnInfo(name = "watering")
    val watering: String?,

    @ColumnInfo(name = "sunlight")
    val sunlight: String?,

    @ColumnInfo(name = "image_url")
    val imageUrl: String? = null

) : Parcelable {
    val defaultImage: Any = R.drawable.plantpal_icon
}


