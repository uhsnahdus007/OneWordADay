package com.onewordaday.app.data.repository

import com.onewordaday.app.data.model.Word
import kotlinx.coroutines.flow.Flow

interface WordRepository {
    suspend fun getTodaysWord(): Word
    suspend fun markWordAsSeen(wordId: Long, date: String)
    suspend fun getWordById(id: Long): Word?
    fun getHistoryWithDates(): Flow<List<Pair<Word, String>>>
    suspend fun getCurrentStreak(): Int
    suspend fun getBestStreak(): Int
    suspend fun getTotalWordsSeen(): Int
    suspend fun toggleFavourite(wordId: Long, current: Boolean)
    fun getFavourites(): Flow<List<Word>>
    suspend fun getFavouritesCount(): Int
}
