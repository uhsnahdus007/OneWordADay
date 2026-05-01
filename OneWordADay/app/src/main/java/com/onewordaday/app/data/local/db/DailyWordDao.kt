package com.onewordaday.app.data.local.db

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Embedded
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
        SELECT w.id, w.word, w.partOfSpeech, w.definition, w.examples, w.theme,
               w.source, w.usedOnDate, w.isFavourited, d.date AS assignedDate
        FROM words w
        INNER JOIN daily_words d ON w.id = d.wordId
        ORDER BY d.date DESC LIMIT 30
    """)
    fun getHistoryWithDates(): Flow<List<WordWithDate>>

    @Query("SELECT date FROM daily_words WHERE seenByUser = 1 ORDER BY date DESC")
    suspend fun getSeenDates(): List<String>

    @Query("SELECT wordId FROM daily_words ORDER BY date DESC LIMIT 30")
    suspend fun getRecentWordIds(): List<Long>

    @Query("SELECT COUNT(*) FROM daily_words WHERE seenByUser = 1")
    suspend fun getSeenCount(): Int
}

data class WordWithDate(
    @Embedded val wordEntity: WordEntity,
    @ColumnInfo(name = "assignedDate") val date: String
)

data class DailyWordSummary(
    val date: String,
    val word: String,
    val theme: String
)
