package com.onewordaday.app.data.repository

import com.onewordaday.app.data.model.Word
import com.onewordaday.app.data.model.WordTheme
import kotlinx.coroutines.flow.Flow

interface WordRepository {
    suspend fun getTodaysWord(): Word
    suspend fun markWordAsSeen(wordId: Long, date: String)
    fun getHistoryWords(): Flow<List<Word>>
}
