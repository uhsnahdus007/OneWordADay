package com.onewordaday.app.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.onewordaday.app.data.local.entity.DailyWordEntity
import com.onewordaday.app.data.local.entity.WordEntity

@Database(
    entities = [WordEntity::class, DailyWordEntity::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun wordDao(): WordDao
    abstract fun dailyWordDao(): DailyWordDao

    companion object {
        const val DATABASE_NAME = "onewordaday.db"

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE words ADD COLUMN isFavourited INTEGER NOT NULL DEFAULT 0")
            }
        }
    }
}
