package com.onewordaday.app.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "daily_words",
    indices = [Index(value = ["date"], unique = true)]
)
data class DailyWordEntity(
    @PrimaryKey val date: String,
    val wordId: Long,
    val theme: String,
    val seenByUser: Boolean = false
)
