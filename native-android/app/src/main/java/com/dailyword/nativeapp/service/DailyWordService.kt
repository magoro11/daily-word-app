package com.dailyword.nativeapp.service

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.dailyword.nativeapp.MainActivity
import com.dailyword.nativeapp.R
import com.dailyword.nativeapp.domain.RotationEngine
import com.dailyword.nativeapp.data.WordDao
import com.dailyword.nativeapp.data.BibleVerse
import com.dailyword.nativeapp.data.MotivationalQuote
import com.dailyword.nativeapp.data.Settings
import com.dailyword.nativeapp.overlay.OverlayService
import com.dailyword.nativeapp.data.UserSettings
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import javax.inject.Inject

@AndroidEntryPoint
class DailyWordService : Service() {
    @Inject lateinit var engine: RotationEngine
    @Inject lateinit var dao: WordDao
    @Inject lateinit var settings: Settings
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        createChannel()
        startForeground(7, buildStatusNotification("Daily Word is ready", "Your next word is scheduled."))
        scope.launch { engine.initialize() }
        observeSettings()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        scope.cancel()
        super.onDestroy()
    }

    private fun createChannel() {
        val nm = getSystemService(NotificationManager::class.java)
        nm.createNotificationChannel(NotificationChannel("daily_word_status", "Daily Word status", NotificationManager.IMPORTANCE_LOW))
        nm.createNotificationChannel(NotificationChannel("daily_word_content", "New content", NotificationManager.IMPORTANCE_DEFAULT))
    }

    private suspend fun observeSettings() {
        settings.flow.collect { prefs ->
            if (prefs.overlayEnabled) OverlayService.start(this@DailyWordService) else OverlayService.stop(this@DailyWordService)
            updateStatusNotification(prefs)
        }
    }

    suspend fun updateStatusNotification(prefs: UserSettings) {
        val verse = dao.latestVerse()
        val quote = dao.latestQuote()
        val text = when {
            !prefs.bibleEnabled && !prefs.quoteEnabled -> "Paused"
            prefs.paused -> "Paused"
            verse != null && quote != null -> "${verse.book} ${verse.chapter}:${verse.verse} · ${quote.author}"
            verse != null -> "${verse.book} ${verse.chapter}:${verse.verse}"
            quote != null -> quote.author
            else -> "Loading your word…"
        }
        val nm = getSystemService(NotificationManager::class.java)
        nm.notify(7, buildStatusNotification("Daily Word", text))
    }

    private fun buildStatusNotification(title: String, text: String): Notification {
        val intent = Intent(this, MainActivity::class.java)
        val pi = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        return NotificationCompat.Builder(this, "daily_word_status")
            .setSmallIcon(android.R.drawable.ic_menu_info_details)
            .setContentTitle(title)
            .setContentText(text)
            .setOngoing(true)
            .setContentIntent(pi)
            .build()
    }

    companion object {
        fun start(context: Context) {
            try { context.startForegroundService(Intent(context, DailyWordService::class.java)) } catch (_: Exception) {}
        }
    }
}
