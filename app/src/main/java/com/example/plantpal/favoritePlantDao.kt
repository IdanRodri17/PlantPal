package com.example.plantpal

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface FavoritePlantDao {


    @Query("SELECT * FROM FavoritePlantsTable")
    fun getAllFavorites(): LiveData<List<FavoritePlant>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(plant: FavoritePlant)

    @Query("DELETE FROM FavoritePlantsTable WHERE id = :id")
    suspend fun deleteById(id: Int)

    @Update
    suspend fun update(plant: FavoritePlant)


    @Query("SELECT EXISTS(SELECT 1 FROM FavoritePlantsTable WHERE id = :plantId)")
    suspend fun isFavorite(plantId: Int): Boolean
}
