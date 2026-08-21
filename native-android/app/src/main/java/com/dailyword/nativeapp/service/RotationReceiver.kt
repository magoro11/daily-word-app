package com.dailyword.nativeapp.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.dailyword.nativeapp.domain.RotationEngine
import com.dailyword.nativeapp.data.WordDao
import com.dailyword.nativeapp.data.Settings
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import javax.inject.Inject

@AndroidEntryPoint
class RotationReceiver : BroadcastReceiver() {
    @Inject lateinit var engine: RotationEngine
    @Inject lateinit var dao: WordDao
    @Inject lateinit var settings: Settings

    override fun onReceive(context: Context, intent: Intent) {
        val pending = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val kind = intent.getStringExtra("kind") ?: "QUOTE"
                if (kind == "SKIP_BIBLE") engine.skip("BIBLE")
                else if (kind == "SKIP_QUOTE") engine.skip("QUOTE")
                else engine.fire(kind)

                val prefs = settings.flow.first()
                if (prefs.notificationsEnabled) {
                    val nm = context.getSystemService(NotificationManager::class.java)
                    nm.createNotificationChannel(NotificationChannel("daily_word_content", "New content", NotificationManager.IMPORTANCE_DEFAULT))
                    val builder = NotificationCompat.Builder(context, "daily_word_content")
                        .setSmallIcon(android.R.drawable.ic_menu_info_details)
                        .setAutoCancel(true)
                        .setContentTitle(if (kind.startsWith("BIBLE")) "New Bible Verse" else "New Motivation")
                    val text = if (kind.startsWith("BIBLE")) {
                        dao.latestVerse()?.let { "${it.text} — ${it.book} ${it.chapter}:${it.verse}" } ?: "Tap to read"
                    } else {
                        dao.latestQuote()?.let { "${it.text} — ${it.author}" } ?: "Tap to read"
                    }
                    builder.setContentText(text)
                    nm.notify(if (kind.startsWith("BIBLE")) 2001 else 2002, builder.build())
                }
            } finally { pending.finish() }
        }
    }
}
