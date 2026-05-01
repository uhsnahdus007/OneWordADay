package com.onewordaday.app.data.local.seeder

import android.content.Context
import com.onewordaday.app.data.local.db.WordDao
import com.onewordaday.app.data.local.entity.WordEntity
import com.onewordaday.app.util.JsonAssetReader
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@JsonClass(generateAdapter = true)
data class WordJson(
    val word: String,
    val partOfSpeech: String,
    val definition: String,
    val examples: List<String>,
    val theme: String
)

class WordDatabaseSeeder @Inject constructor(
    @ApplicationContext private val context: Context,
    private val wordDao: WordDao,
    private val moshi: Moshi
) {
    suspend fun seedIfEmpty() {
        if (wordDao.count() > 0) return

        val json = JsonAssetReader.read(context, "words.json")
        val listType = Types.newParameterizedType(List::class.java, WordJson::class.java)
        val adapter = moshi.adapter<List<WordJson>>(listType)
        val wordJsonList = adapter.fromJson(json) ?: return

        val entities = wordJsonList.map { wj ->
            val examplesJson = moshi.adapter<List<String>>(
                Types.newParameterizedType(List::class.java, String::class.java)
            ).toJson(wj.examples)

            WordEntity(
                word = wj.word,
                partOfSpeech = wj.partOfSpeech,
                definition = wj.definition,
                examples = examplesJson,
                theme = wj.theme,
                source = "bundled"
            )
        }

        wordDao.insertAll(entities)
    }
}
