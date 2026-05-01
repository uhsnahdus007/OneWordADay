package com.onewordaday.app.data.local.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.onewordaday.app.data.local.entity.DailyWordEntity
import com.onewordaday.app.data.local.entity.WordEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DailyWordDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(dailyWord: DailyWordEntity)

    @Update
    suspend fun update(dailyWord: DailyWordEntity)

    @Query("SELECT * FROM daily_words WHERE date = :date")
    suspend fun getByDate(date: String): DailyWordEntity?

    @Query("""
        SELECT w.* FROM words w
        INNER JOIN daily_words d ON w.id = d.wordId
        ORDER BY d.date DESC LIMIT 30
    """)
    fun getHistoryWords(): Flow<List<WordEntity>>

    @Query("""
        SELECT d.date, w.word, w.theme FROM daily_words d
        INNER JOIN words w ON w.id = d.wordId
        ORDER BY d.date DESC LIMIT 30
    """)
    fun getRecentDates(): Flow<List<DailyWordSummary>>

    @Query("SELECT wordId FROM daily_words ORDER BY date DESC LIMIT 30")
    suspend fun getRecentWordIds(): List<Long>
}

data class DailyWordSummary(
    val date: String,
    val word: String,
    val theme: String
)
