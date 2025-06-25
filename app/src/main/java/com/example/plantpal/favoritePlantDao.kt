package com.example.plantpal

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface FavoritePlantDao {

<<<<<<< HEAD

=======
>>>>>>> 70b5208f6b4e5f358a23068484823b97a630b2a8
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
