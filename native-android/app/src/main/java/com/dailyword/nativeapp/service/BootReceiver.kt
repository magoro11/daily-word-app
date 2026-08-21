package com.dailyword.nativeapp.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.dailyword.nativeapp.domain.RotationEngine
import com.dailyword.nativeapp.service.DailyWordService
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import javax.inject.Inject

@AndroidEntryPoint
class BootReceiver : BroadcastReceiver() {
    @Inject lateinit var engine: RotationEngine
    override fun onReceive(context: Context, intent: Intent) {
        val pending = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                if (intent.action == Intent.ACTION_BOOT_COMPLETED || intent.action == Intent.ACTION_MY_PACKAGE_REPLACED) {
                    engine.initialize()
                    DailyWordService.start(context)
                }
            } finally { pending.finish() }
        }
    }
}
