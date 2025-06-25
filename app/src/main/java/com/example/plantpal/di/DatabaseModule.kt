package com.example.plantpal.di

import android.content.Context
import androidx.room.Room
import com.example.plantpal.AppDatabase
import com.example.plantpal.FavoritePlantDao
<<<<<<< HEAD
import com.example.plantpal.model.CachedApiPlantDao
=======
>>>>>>> 70b5208f6b4e5f358a23068484823b97a630b2a8
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
<<<<<<< HEAD
=======
        // Clear the database file if it exists
        context.deleteDatabase("plant_pal_db")
        
>>>>>>> 70b5208f6b4e5f358a23068484823b97a630b2a8
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "plant_pal_db"
<<<<<<< HEAD
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    fun provideFavoritePlantDao(database: AppDatabase): FavoritePlantDao {
        return database.favoritePlantDao()
    }

    @Provides
    fun provideCachedApiPlantDao(database: AppDatabase): CachedApiPlantDao {
        return database.cachedApiPlantDao()
    }

=======
        )
        .addMigrations(AppDatabase.MIGRATION_2_3)
        .fallbackToDestructiveMigration()
        .build()
    }

    @Provides
    @Singleton
    fun provideFavoritePlantDao(database: AppDatabase): FavoritePlantDao {
        return database.favoritePlantDao()
    }
>>>>>>> 70b5208f6b4e5f358a23068484823b97a630b2a8
}
