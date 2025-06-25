package com.example.plantpal

<<<<<<< HEAD
import android.content.Context
import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.plantpal.model.CachedApiPlant
import com.example.plantpal.model.CachedApiPlantDao


@Database(entities = [FavoritePlant::class, CachedApiPlant::class], version = 2)
abstract class AppDatabase : RoomDatabase() {
    abstract fun favoritePlantDao(): FavoritePlantDao
    abstract fun cachedApiPlantDao(): CachedApiPlantDao


=======
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [FavoritePlant::class],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun favoritePlantDao(): FavoritePlantDao

    companion object {
        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Drop and recreate the table to ensure clean state
                database.execSQL("DROP TABLE IF EXISTS FavoritePlantsTable")
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS FavoritePlantsTable (
                        id INTEGER PRIMARY KEY NOT NULL,
                        common_name TEXT,
                        scientific_name TEXT,
                        image_url TEXT
                    )
                """)
            }
        }
    }
>>>>>>> 70b5208f6b4e5f358a23068484823b97a630b2a8
}
