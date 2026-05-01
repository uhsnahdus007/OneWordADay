package com.onewordaday.app.di

import android.content.Context
import androidx.room.Room
import com.onewordaday.app.data.local.db.AppDatabase
import com.onewordaday.app.data.local.db.DailyWordDao
import com.onewordaday.app.data.local.db.WordDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, AppDatabase.DATABASE_NAME)
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun provideWordDao(db: AppDatabase): WordDao = db.wordDao()

    @Provides
    fun provideDailyWordDao(db: AppDatabase): DailyWordDao = db.dailyWordDao()
}
