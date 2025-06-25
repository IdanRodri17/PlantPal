package com.example.plantpal

<<<<<<< HEAD
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "FavoritePlantsTable")
data class FavoritePlant(
    @PrimaryKey(autoGenerate = true)
=======
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "FavoritePlantsTable")
data class FavoritePlant(
    @PrimaryKey
>>>>>>> 70b5208f6b4e5f358a23068484823b97a630b2a8
    val id: Int,

    @ColumnInfo(name = "common_name")
    val commonName: String?,

    @ColumnInfo(name = "scientific_name")
    val scientificName: String?,

    @ColumnInfo(name = "image_url")
<<<<<<< HEAD
    val imageUrl: String? = null,

    @ColumnInfo(name = "Watering")
    val watering: String?,

    @ColumnInfo(name = "Sunlight")
    val sunlight: String?
) : Parcelable
=======
    val imageUrl: String? = null
)
>>>>>>> 70b5208f6b4e5f358a23068484823b97a630b2a8
