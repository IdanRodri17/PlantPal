package com.example.plantpal.model

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface CachedApiPlantDao {

    @Query("SELECT * FROM CachedApiPlantsTable")
    fun getAllCached(): LiveData<List<CachedApiPlant>>

    @Query("SELECT * FROM CachedApiPlantsTable")
    suspend fun getAllCachedDirect(): List<CachedApiPlant>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(plants: List<CachedApiPlant>)

    @Query("DELETE FROM CachedApiPlantsTable")
    suspend fun clearCache()
}
