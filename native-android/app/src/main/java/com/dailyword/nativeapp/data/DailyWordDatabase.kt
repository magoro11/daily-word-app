package com.dailyword.nativeapp.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao interface WordDao {
    @Query("SELECT * FROM BibleVerse WHERE favorite = 1") fun favoriteVerses(): Flow<List<BibleVerse>>
    @Query("SELECT * FROM MotivationalQuote WHERE favorite = 1") fun favoriteQuotes(): Flow<List<MotivationalQuote>>
    @Query("SELECT * FROM BibleVerse") suspend fun verses(): List<BibleVerse>
    @Query("SELECT * FROM MotivationalQuote") suspend fun quotes(): List<MotivationalQuote>
    @Query("SELECT * FROM BibleVerse WHERE category = :cat") suspend fun versesByCategory(cat: String): List<BibleVerse>
    @Query("SELECT * FROM MotivationalQuote WHERE category = :cat") suspend fun quotesByCategory(cat: String): List<MotivationalQuote>
    @Query("SELECT BibleVerse.* FROM HistoryEntry JOIN BibleVerse ON HistoryEntry.bibleVerseId = BibleVerse.id WHERE bibleVerseId IS NOT NULL ORDER BY displayedAt DESC LIMIT 1") suspend fun latestVerse(): BibleVerse?
    @Query("SELECT MotivationalQuote.* FROM HistoryEntry JOIN MotivationalQuote ON HistoryEntry.quoteId = MotivationalQuote.id WHERE quoteId IS NOT NULL ORDER BY displayedAt DESC LIMIT 1") suspend fun latestQuote(): MotivationalQuote?
    @Query("SELECT * FROM HistoryEntry ORDER BY displayedAt DESC LIMIT :limit") suspend fun history(limit: Int = 100): List<HistoryEntry>
    @Query("SELECT * FROM HistoryEntry WHERE bibleVerseId = :id OR quoteId = :id ORDER BY displayedAt DESC") suspend fun historyForItem(id: String): List<HistoryEntry>
    @Query("SELECT * FROM BibleVerse WHERE text LIKE '%' || :q || '%' OR book LIKE '%' || :q || '%' ORDER BY lastDisplayedAt DESC") suspend fun searchVerses(q: String): List<BibleVerse>
    @Query("SELECT * FROM MotivationalQuote WHERE text LIKE '%' || :q || '%' OR author LIKE '%' || :q || '%' ORDER BY lastDisplayedAt DESC") suspend fun searchQuotes(q: String): List<MotivationalQuote>
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun insertVerses(items: List<BibleVerse>)
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun insertQuotes(items: List<MotivationalQuote>)
    @Insert suspend fun history(item: HistoryEntry)
    @Query("UPDATE BibleVerse SET favorite = NOT favorite WHERE id = :id") suspend fun toggleVerse(id: String)
    @Query("UPDATE MotivationalQuote SET favorite = NOT favorite WHERE id = :id") suspend fun toggleQuote(id: String)
    @Query("UPDATE BibleVerse SET lastDisplayedAt = :at WHERE id = :id") suspend fun touchVerse(id: String, at: Long)
    @Query("UPDATE MotivationalQuote SET lastDisplayedAt = :at WHERE id = :id") suspend fun touchQuote(id: String, at: Long)
    @Query("SELECT COUNT(*) FROM BibleVerse") suspend fun verseCount(): Int
    @Query("SELECT COUNT(*) FROM MotivationalQuote") suspend fun quoteCount(): Int
    @Query("SELECT DISTINCT category FROM BibleVerse") suspend fun bibleCategories(): List<String>
    @Query("SELECT DISTINCT category FROM MotivationalQuote") suspend fun motivationCategories(): List<String>
}

@Database(entities = [BibleVerse::class, MotivationalQuote::class, HistoryEntry::class], version = 1, exportSchema = false)
abstract class DailyWordDatabase : RoomDatabase() { abstract fun words(): WordDao }
