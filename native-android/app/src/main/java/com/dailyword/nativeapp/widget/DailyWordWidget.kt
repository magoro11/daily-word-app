package com.dailyword.nativeapp.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import androidx.room.Room
import com.dailyword.nativeapp.MainActivity
import com.dailyword.nativeapp.R
import com.dailyword.nativeapp.data.DailyWordDatabase
import kotlinx.coroutines.runBlocking

class DailyWordWidget : AppWidgetProvider() {
    override fun onUpdate(context: Context, manager: AppWidgetManager, ids: IntArray) {
        ids.forEach { update(context, manager, it) }
    }
    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if (intent.action == "com.dailyword.nativeapp.SKIP_BIBLE" || intent.action == "com.dailyword.nativeapp.SKIP_QUOTE") {
            update(context)
        }
    }
    companion object {
        fun update(context: Context, manager: AppWidgetManager? = null, id: Int? = null) = runBlocking {
            val db = Room.databaseBuilder(context.applicationContext, DailyWordDatabase::class.java, "daily-word.db").build()
            val verse = db.words().latestVerse()
            val quote = db.words().latestQuote()
            val views = RemoteViews(context.packageName, R.layout.widget_daily_word).apply {
                setTextViewText(R.id.widget_verse, verse?.let { "${it.text} — ${it.book} ${it.chapter}:${it.verse}" } ?: "Open Daily Word to begin")
                setTextViewText(R.id.widget_quote, quote?.let { "${it.text} — ${it.author}" } ?: "A steady word for your day")
                val openApp = android.app.PendingIntent.getActivity(context, 0, Intent(context, MainActivity::class.java), android.app.PendingIntent.FLAG_IMMUTABLE)
                val skipVerse = android.app.PendingIntent.getBroadcast(context, 1003, Intent(context, DailyWordWidget::class.java).setAction("com.dailyword.nativeapp.SKIP_BIBLE"), android.app.PendingIntent.FLAG_IMMUTABLE)
                val skipQuote = android.app.PendingIntent.getBroadcast(context, 1004, Intent(context, DailyWordWidget::class.java).setAction("com.dailyword.nativeapp.SKIP_QUOTE"), android.app.PendingIntent.FLAG_IMMUTABLE)
                setOnClickPendingIntent(R.id.widget_root, openApp)
                setOnClickPendingIntent(R.id.widget_verse, skipVerse)
                setOnClickPendingIntent(R.id.widget_quote, skipQuote)
            }
            val app = manager ?: AppWidgetManager.getInstance(context)
            if (id != null) app.updateAppWidget(id, views) else app.updateAppWidget(android.content.ComponentName(context, DailyWordWidget::class.java), views)
            db.close()
        }
    }
}
