package com.onewordaday.app.data.model

data class Word(
    val id: Long,
    val word: String,
    val partOfSpeech: String,
    val definition: String,
    val examples: List<String>,
    val theme: WordTheme,
    val source: String
)
