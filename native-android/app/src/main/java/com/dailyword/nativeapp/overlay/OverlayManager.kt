package com.dailyword.nativeapp.overlay

import android.content.Context
import android.provider.Settings

/** Overlay entry point. Call only after the user explicitly grants Display over other apps. */
class OverlayManager(private val context: Context) {
    fun canDraw(): Boolean = Settings.canDrawOverlays(context)
    // WindowManager attachment is deliberately owned by a user-enabled service, never started silently.
}
