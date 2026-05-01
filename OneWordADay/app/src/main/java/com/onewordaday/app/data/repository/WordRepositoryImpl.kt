package com.onewordaday.app.data.repository

import com.onewordaday.app.data.local.db.DailyWordDao
import com.onewordaday.app.data.local.db.WordDao
import com.onewordaday.app.data.local.entity.DailyWordEntity
import com.onewordaday.app.data.local.seeder.WordDatabaseSeeder
import com.onewordaday.app.data.model.Word
import com.onewordaday.app.data.model.WordTheme
import com.onewordaday.app.util.DateUtils
import com.onewordaday.app.util.PreferencesManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
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
            wordDao.resetBundledWordsForTheme(WordTheme.GENERAL.key)
            val fallback = wordDao.getUnusedWordByTheme(WordTheme.GENERAL.key)!!
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

    override suspend fun getWordById(id: Long): Word? = wordDao.getById(id)?.toDomain()

    override fun getHistoryWithDates(): Flow<List<Pair<Word, String>>> =
        dailyWordDao.getHistoryWithDates().map { list ->
            list.map { it.wordEntity.toDomain() to it.date }
        }

    override suspend fun getCurrentStreak(): Int {
        val seenDates = dailyWordDao.getSeenDates()
        return calculateStreak(seenDates, fromStart = false)
    }

    override suspend fun getBestStreak(): Int {
        val seenDates = dailyWordDao.getSeenDates()
        if (seenDates.isEmpty()) return 0

        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val cal = Calendar.getInstance()
        var best = 1
        var current = 1

        for (i in 1 until seenDates.size) {
            val prev = formatter.parse(seenDates[i - 1]) ?: continue
            cal.time = prev
            cal.add(Calendar.DAY_OF_YEAR, -1)
            if (formatter.format(cal.time) == seenDates[i]) {
                current++
                if (current > best) best = current
            } else {
                current = 1
            }
        }
        return best
    }

    override suspend fun getTotalWordsSeen(): Int = dailyWordDao.getSeenCount()

    override suspend fun toggleFavourite(wordId: Long, current: Boolean) {
        wordDao.setFavourited(wordId, !current)
    }

    override fun getFavourites(): Flow<List<Word>> =
        wordDao.getFavourites().map { list -> list.map { it.toDomain() } }

    override suspend fun getFavouritesCount(): Int = wordDao.countFavourites()

    private fun calculateStreak(seenDates: List<String>, fromStart: Boolean): Int {
        if (seenDates.isEmpty()) return 0

        val today = DateUtils.today()
        val yesterday = DateUtils.yesterday()
        if (seenDates.first() != today && seenDates.first() != yesterday) return 0

        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val cal = Calendar.getInstance()
        var streak = 0
        var expected = seenDates.first()

        for (date in seenDates) {
            if (date == expected) {
                streak++
                val parsed = formatter.parse(expected) ?: break
                cal.time = parsed
                cal.add(Calendar.DAY_OF_YEAR, -1)
                expected = formatter.format(cal.time)
            } else {
                break
            }
        }
        return streak
    }
}
