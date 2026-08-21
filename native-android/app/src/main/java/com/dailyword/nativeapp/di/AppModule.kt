package com.dailyword.nativeapp.di

import android.content.Context
import androidx.room.Room
import com.dailyword.nativeapp.data.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module @InstallIn(SingletonComponent::class) object AppModule {
    @Provides @Singleton fun database(@ApplicationContext context: Context) = Room.databaseBuilder(context, DailyWordDatabase::class.java, "daily-word.db").build()
    @Provides fun dao(db: DailyWordDatabase) = db.words()
    @Provides @Singleton fun settings(@ApplicationContext context: Context) = Settings(context)
}
