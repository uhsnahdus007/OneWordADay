package com.onewordaday.app.data.repository

import com.onewordaday.app.data.local.db.DailyWordDao
import com.onewordaday.app.data.local.db.WordDao
import com.onewordaday.app.data.local.entity.DailyWordEntity
import com.onewordaday.app.data.local.seeder.WordDatabaseSeeder
import com.onewordaday.app.data.model.Word
import com.onewordaday.app.util.DateUtils
import com.onewordaday.app.util.PreferencesManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WordRepositoryImpl @Inject constructor(
    private val wordDao: WordDao,
    private val dailyWordDao: DailyWordDao,
    private val preferencesManager: PreferencesManager,
    private val seeder: WordDatabaseSeeder
) : WordRepository {

    override suspend fun getTodaysWord(): Word {
        seeder.seedIfEmpty()

        val today = DateUtils.today()
        val existing = dailyWordDao.getByDate(today)

        if (existing != null) {
            return wordDao.getById(existing.wordId)!!.toDomain()
        }

        return assignWordForToday(today)
    }

    private suspend fun assignWordForToday(date: String): Word {
        val theme = preferencesManager.getSelectedTheme()

        var unusedCount = wordDao.countUnusedByTheme(theme.key)
        if (unusedCount == 0) {
            wordDao.resetBundledWordsForTheme(theme.key)
            unusedCount = wordDao.countUnusedByTheme(theme.key)
        }

        if (unusedCount == 0) {
            val fallbackTheme = com.onewordaday.app.data.model.WordTheme.GENERAL
            wordDao.resetBundledWordsForTheme(fallbackTheme.key)
            val fallback = wordDao.getUnusedWordByTheme(fallbackTheme.key)!!
            wordDao.markAsUsed(fallback.id, date)
            dailyWordDao.upsert(DailyWordEntity(date, fallback.id, fallback.theme))
            return fallback.toDomain()
        }

        val entity = wordDao.getUnusedWordByTheme(theme.key)!!
        wordDao.markAsUsed(entity.id, date)
        dailyWordDao.upsert(DailyWordEntity(date, entity.id, theme.key))
        return entity.toDomain()
    }

    override suspend fun markWordAsSeen(wordId: Long, date: String) {
        val entry = dailyWordDao.getByDate(date) ?: return
        if (!entry.seenByUser) {
            dailyWordDao.upsert(entry.copy(seenByUser = true))
        }
    }

    override fun getHistoryWords(): Flow<List<Word>> =
        dailyWordDao.getHistoryWords().map { list -> list.map { it.toDomain() } }
}
