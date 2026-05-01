package com.onewordaday.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.onewordaday.app.data.model.Word
import com.onewordaday.app.data.model.WordTheme
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types

@Entity(tableName = "words")
data class WordEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val word: String,
    val partOfSpeech: String,
    val definition: String,
    val examples: String,
    val theme: String,
    val source: String = "bundled",
    val usedOnDate: String? = null,
    val isFavourited: Boolean = false
) {
    fun toDomain(): Word {
        val moshi = Moshi.Builder().build()
        val listType = Types.newParameterizedType(List::class.java, String::class.java)
        val adapter = moshi.adapter<List<String>>(listType)
        val exampleList = try {
            adapter.fromJson(examples) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
        return Word(
            id = id,
            word = word,
            partOfSpeech = partOfSpeech,
            definition = definition,
            examples = exampleList,
            theme = WordTheme.fromKey(theme),
            source = source,
            isFavourited = isFavourited
        )
    }
}
