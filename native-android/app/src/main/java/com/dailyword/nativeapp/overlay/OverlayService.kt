package com.dailyword.nativeapp.overlay

import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.IBinder
import android.view.*
import android.widget.FrameLayout
import com.dailyword.nativeapp.R
import com.dailyword.nativeapp.data.WordDao
import com.dailyword.nativeapp.data.BibleVerse
import com.dailyword.nativeapp.data.MotivationalQuote
import kotlinx.coroutines.*
import javax.inject.Inject
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OverlayService : Service() {
    @Inject lateinit var dao: WordDao
    private lateinit var windowManager: WindowManager
    private lateinit var overlayView: View
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private var isClickThrough = false

    override fun onCreate() {
        super.onCreate()
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        overlayView = LayoutInflater.from(this).inflate(R.layout.overlay_bubble, null)
        setupOverlay()
        scope.launch { updateContent() }
    }

    private fun setupOverlay() {
        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O)
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            else WindowManager.LayoutParams.TYPE_PHONE,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            PixelFormat.TRANSLUCENT
        )
        params.gravity = Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
        params.y = 100
        params.x = 0

        overlayView.findViewById<FrameLayout>(R.id.overlay_container).setOnTouchListener(OverlayTouchListener(params))
        overlayView.findViewById<View>(R.id.overlay_expand).setOnClickListener {
            startActivity(Intent(this, com.dailyword.nativeapp.overlay.OverlayActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        }

        windowManager.addView(overlayView, params)
    }

    suspend fun updateContent() {
        val verse = dao.latestVerse()
        val quote = dao.latestQuote()
        val text = overlayView.findViewById<android.widget.TextView>(R.id.overlay_text)
        val sub = overlayView.findViewById<android.widget.TextView>(R.id.overlay_sub)
        withContext(Dispatchers.Main) {
            text.text = verse?.text ?: quote?.text ?: "Daily Word"
            sub.text = when {
                verse != null -> "${verse.book} ${verse.chapter}:${verse.verse}"
                quote != null -> quote.author
                else -> ""
            }
        }
    }

    fun setClickThrough(enabled: Boolean) {
        isClickThrough = enabled
        if (!::overlayView.isInitialized) return
        val params = overlayView.layoutParams as WindowManager.LayoutParams
        params.flags = if (enabled)
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        else
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        windowManager.updateViewLayout(overlayView, params)
    }

    fun setPosition(position: String) {
        if (!::overlayView.isInitialized) return
        val params = overlayView.layoutParams as WindowManager.LayoutParams
        params.gravity = when (position) {
            "top" -> Gravity.TOP or Gravity.CENTER_HORIZONTAL
            "center" -> Gravity.CENTER or Gravity.CENTER_HORIZONTAL
            else -> Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
        }
        windowManager.updateViewLayout(overlayView, params)
    }

    fun setOpacity(opacity: Float) {
        if (!::overlayView.isInitialized) return
        overlayView.alpha = opacity
    }

    override fun onDestroy() {
        scope.cancel()
        if (::overlayView.isInitialized) windowManager.removeView(overlayView)
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    companion object {
        fun start(context: Context) {
            try { context.startService(Intent(context, OverlayService::class.java)) } catch (_: Exception) {}
        }
        fun stop(context: Context) {
            context.stopService(Intent(context, OverlayService::class.java))
        }
    }

    inner class OverlayTouchListener(private val params: WindowManager.LayoutParams) : View.OnTouchListener {
        private var initialY = 0
        private var initialTouchY = 0f
        private var isDragging = false

        override fun onTouch(v: View, event: MotionEvent): Boolean {
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    initialY = params.y
                    initialTouchY = event.rawY
                    isDragging = false
                }
                MotionEvent.ACTION_MOVE -> {
                    val dy = event.rawY - initialTouchY
                    if (Math.abs(dy) > 10) isDragging = true
                    params.y = initialY - dy.toInt()
                    windowManager.updateViewLayout(overlayView, params)
                }
                MotionEvent.ACTION_UP -> {
                    if (!isDragging) v.performClick()
                }
            }
            return isDragging
        }
    }
}
