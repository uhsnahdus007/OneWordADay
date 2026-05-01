package com.onewordaday.app.data.local.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.onewordaday.app.data.local.entity.WordEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WordDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(words: List<WordEntity>): List<Long>

    @Query("SELECT * FROM words WHERE theme = :theme AND usedOnDate IS NULL ORDER BY RANDOM() LIMIT 1")
    suspend fun getUnusedWordByTheme(theme: String): WordEntity?

    @Query("SELECT * FROM words WHERE id = :id")
    suspend fun getById(id: Long): WordEntity?

    @Query("UPDATE words SET usedOnDate = :date WHERE id = :id")
    suspend fun markAsUsed(id: Long, date: String)

    @Query("SELECT COUNT(*) FROM words WHERE theme = :theme AND usedOnDate IS NULL")
    suspend fun countUnusedByTheme(theme: String): Int

    @Query("UPDATE words SET usedOnDate = NULL WHERE theme = :theme AND source = 'bundled'")
    suspend fun resetBundledWordsForTheme(theme: String)

    @Query("SELECT COUNT(*) FROM words")
    suspend fun count(): Int

    @Query("UPDATE words SET isFavourited = :favourited WHERE id = :id")
    suspend fun setFavourited(id: Long, favourited: Boolean)

    @Query("SELECT * FROM words WHERE isFavourited = 1 ORDER BY word ASC")
    fun getFavourites(): Flow<List<WordEntity>>

    @Query("SELECT COUNT(*) FROM words WHERE isFavourited = 1")
    suspend fun countFavourites(): Int
}
