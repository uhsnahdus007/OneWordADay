package com.onewordaday.app.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.onewordaday.app.data.local.entity.DailyWordEntity
import com.onewordaday.app.data.local.entity.WordEntity

@Database(
    entities = [WordEntity::class, DailyWordEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun wordDao(): WordDao
    abstract fun dailyWordDao(): DailyWordDao

    companion object {
        const val DATABASE_NAME = "onewordaday.db"
    }
}
