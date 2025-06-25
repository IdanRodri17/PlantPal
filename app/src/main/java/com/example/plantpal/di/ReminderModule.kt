package com.example.plantpal.di

import android.content.Context
import com.example.plantpal.util.WateringReminderScheduler
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ReminderModule {

    @Provides
    @Singleton
    fun provideWateringReminderScheduler(
        @ApplicationContext context: Context
    ): WateringReminderScheduler {
        return WateringReminderScheduler(context)
    }
} 