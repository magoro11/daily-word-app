package com.dailyword.nativeapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity data class BibleVerse(
    @PrimaryKey val id: String,
    val book: String,
    val chapter: Int,
    val verse: Int,
    val translation: String = "KJV",
    val text: String,
    val category: String,
    val favorite: Boolean = false,
    val lastDisplayedAt: Long? = null
)

@Entity data class MotivationalQuote(
    @PrimaryKey val id: String,
    val text: String,
    val author: String,
    val category: String,
    val favorite: Boolean = false,
    val lastDisplayedAt: Long? = null
)

@Entity data class HistoryEntry(
    @PrimaryKey val id: String,
    val bibleVerseId: String?,
    val quoteId: String?,
    val displayedAt: Long
)
