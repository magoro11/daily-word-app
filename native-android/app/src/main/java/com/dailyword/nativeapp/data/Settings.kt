package com.dailyword.nativeapp.data

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore("daily_word_settings")

@Singleton
class Settings @Inject constructor(private val context: Context) {
    private object K {
        val bibleEnabled = booleanPreferencesKey("bible_enabled")
        val bibleMinutes = intPreferencesKey("bible_minutes")
        val bibleTranslation = stringPreferencesKey("bible_translation")
        val bibleRandom = booleanPreferencesKey("bible_random")
        val bibleCategories = stringPreferencesKey("bible_categories")

        val quoteEnabled = booleanPreferencesKey("quote_enabled")
        val quoteMinutes = intPreferencesKey("quote_minutes")
        val quoteRandom = booleanPreferencesKey("quote_random")
        val quoteCategories = stringPreferencesKey("quote_categories")

        val paused = booleanPreferencesKey("paused")
        val nextBibleAt = longPreferencesKey("next_bible_at")
        val nextQuoteAt = longPreferencesKey("next_quote_at")

        val theme = stringPreferencesKey("theme")
        val fontScale = floatPreferencesKey("font_scale")
        val dynamicColor = booleanPreferencesKey("dynamic_color")
        val overlayEnabled = booleanPreferencesKey("overlay_enabled")
        val overlayPosition = stringPreferencesKey("overlay_position")
        val overlayOpacity = floatPreferencesKey("overlay_opacity")
        val overlayClickThrough = booleanPreferencesKey("overlay_click_through")

        val notificationsEnabled = booleanPreferencesKey("notifications_enabled")
        val startWithSystem = booleanPreferencesKey("start_with_system")
        val onboardingDone = booleanPreferencesKey("onboarding_done")

        val scheduleEnabled = booleanPreferencesKey("schedule_enabled")
        val dailyThemeEnabled = booleanPreferencesKey("daily_theme_enabled")
    }

    val flow: Flow<UserSettings> = context.dataStore.data.map { prefs ->
        UserSettings(
            bibleEnabled = prefs[K.bibleEnabled] ?: true,
            bibleMinutes = prefs[K.bibleMinutes] ?: 60,
            bibleTranslation = prefs[K.bibleTranslation] ?: "KJV",
            bibleRandom = prefs[K.bibleRandom] ?: true,
            bibleCategories = prefs[K.bibleCategories]?.split(",")?.toSet() ?: emptySet(),

            quoteEnabled = prefs[K.quoteEnabled] ?: true,
            quoteMinutes = prefs[K.quoteMinutes] ?: 5,
            quoteRandom = prefs[K.quoteRandom] ?: true,
            quoteCategories = prefs[K.quoteCategories]?.split(",")?.toSet() ?: emptySet(),

            paused = prefs[K.paused] ?: false,
            nextBibleAt = prefs[K.nextBibleAt] ?: 0L,
            nextQuoteAt = prefs[K.nextQuoteAt] ?: 0L,

            theme = prefs[K.theme] ?: "system",
            fontScale = prefs[K.fontScale] ?: 1.0f,
            dynamicColor = prefs[K.dynamicColor] ?: true,
            overlayEnabled = prefs[K.overlayEnabled] ?: false,
            overlayPosition = prefs[K.overlayPosition] ?: "bottom",
            overlayOpacity = prefs[K.overlayOpacity] ?: 0.85f,
            overlayClickThrough = prefs[K.overlayClickThrough] ?: false,

            notificationsEnabled = prefs[K.notificationsEnabled] ?: true,
            startWithSystem = prefs[K.startWithSystem] ?: true,
            onboardingDone = prefs[K.onboardingDone] ?: false,

            scheduleEnabled = prefs[K.scheduleEnabled] ?: false,
            dailyThemeEnabled = prefs[K.dailyThemeEnabled] ?: false
        )
    }

    suspend fun update(block: (UserSettings) -> UserSettings) {
        context.dataStore.edit { prefs ->
            val current = UserSettings(
                bibleEnabled = prefs[K.bibleEnabled] ?: true,
                bibleMinutes = prefs[K.bibleMinutes] ?: 60,
                bibleTranslation = prefs[K.bibleTranslation] ?: "KJV",
                bibleRandom = prefs[K.bibleRandom] ?: true,
                bibleCategories = prefs[K.bibleCategories]?.split(",")?.toSet() ?: emptySet(),
                quoteEnabled = prefs[K.quoteEnabled] ?: true,
                quoteMinutes = prefs[K.quoteMinutes] ?: 5,
                quoteRandom = prefs[K.quoteRandom] ?: true,
                quoteCategories = prefs[K.quoteCategories]?.split(",")?.toSet() ?: emptySet(),
                paused = prefs[K.paused] ?: false,
                nextBibleAt = prefs[K.nextBibleAt] ?: 0L,
                nextQuoteAt = prefs[K.nextQuoteAt] ?: 0L,
                theme = prefs[K.theme] ?: "system",
                fontScale = prefs[K.fontScale] ?: 1.0f,
                dynamicColor = prefs[K.dynamicColor] ?: true,
                overlayEnabled = prefs[K.overlayEnabled] ?: false,
                overlayPosition = prefs[K.overlayPosition] ?: "bottom",
                overlayOpacity = prefs[K.overlayOpacity] ?: 0.85f,
                overlayClickThrough = prefs[K.overlayClickThrough] ?: false,
                notificationsEnabled = prefs[K.notificationsEnabled] ?: true,
                startWithSystem = prefs[K.startWithSystem] ?: true,
                onboardingDone = prefs[K.onboardingDone] ?: false,
                scheduleEnabled = prefs[K.scheduleEnabled] ?: false,
                dailyThemeEnabled = prefs[K.dailyThemeEnabled] ?: false
            )
            val next = block(current)
            prefs[K.bibleEnabled] = next.bibleEnabled
            prefs[K.bibleMinutes] = next.bibleMinutes
            prefs[K.bibleTranslation] = next.bibleTranslation
            prefs[K.bibleRandom] = next.bibleRandom
            prefs[K.bibleCategories] = next.bibleCategories.joinToString(",")
            prefs[K.quoteEnabled] = next.quoteEnabled
            prefs[K.quoteMinutes] = next.quoteMinutes
            prefs[K.quoteRandom] = next.quoteRandom
            prefs[K.quoteCategories] = next.quoteCategories.joinToString(",")
            prefs[K.paused] = next.paused
            prefs[K.nextBibleAt] = next.nextBibleAt
            prefs[K.nextQuoteAt] = next.nextQuoteAt
            prefs[K.theme] = next.theme
            prefs[K.fontScale] = next.fontScale
            prefs[K.dynamicColor] = next.dynamicColor
            prefs[K.overlayEnabled] = next.overlayEnabled
            prefs[K.overlayPosition] = next.overlayPosition
            prefs[K.overlayOpacity] = next.overlayOpacity
            prefs[K.overlayClickThrough] = next.overlayClickThrough
            prefs[K.notificationsEnabled] = next.notificationsEnabled
            prefs[K.startWithSystem] = next.startWithSystem
            prefs[K.onboardingDone] = next.onboardingDone
            prefs[K.scheduleEnabled] = next.scheduleEnabled
            prefs[K.dailyThemeEnabled] = next.dailyThemeEnabled
        }
    }
}

data class UserSettings(
    val bibleEnabled: Boolean = true,
    val bibleMinutes: Int = 60,
    val bibleTranslation: String = "KJV",
    val bibleRandom: Boolean = true,
    val bibleCategories: Set<String> = emptySet(),

    val quoteEnabled: Boolean = true,
    val quoteMinutes: Int = 5,
    val quoteRandom: Boolean = true,
    val quoteCategories: Set<String> = emptySet(),

    val paused: Boolean = false,
    val nextBibleAt: Long = 0L,
    val nextQuoteAt: Long = 0L,

    val theme: String = "system",
    val fontScale: Float = 1.0f,
    val dynamicColor: Boolean = true,
    val overlayEnabled: Boolean = false,
    val overlayPosition: String = "bottom",
    val overlayOpacity: Float = 0.85f,
    val overlayClickThrough: Boolean = false,

    val notificationsEnabled: Boolean = true,
    val startWithSystem: Boolean = true,
    val onboardingDone: Boolean = false,

    val scheduleEnabled: Boolean = false,
    val dailyThemeEnabled: Boolean = false
)
