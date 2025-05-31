package com.example.plantpal.di

import android.content.Context
import androidx.room.Room
import com.example.plantpal.AppDatabase
import com.example.plantpal.FavoritePlantDao
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
        // Clear the database file if it exists
        context.deleteDatabase("plant_pal_db")
        
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "plant_pal_db"
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
}
