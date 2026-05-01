package com.onewordaday.app.data.model

enum class WordTheme(val displayName: String, val key: String) {
    GENERAL("General English", "GENERAL"),
    GOT("Game of Thrones", "GOT"),
    HIMYM("How I Met Your Mother", "HIMYM"),
    OFFICE("The Office", "OFFICE"),
    BREAKING_BAD("Breaking Bad", "BB"),
    FRIENDS("Friends", "FRIENDS");

    companion object {
        fun fromKey(key: String): WordTheme = entries.find { it.key == key } ?: GENERAL
    }
}
