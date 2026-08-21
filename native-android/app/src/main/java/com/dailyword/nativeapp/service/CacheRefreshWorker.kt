package com.dailyword.nativeapp.service

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

/** Reserved offline cache-refresh work. Rotation itself is alarm-driven so 1/5/10 minute choices remain valid. */
class CacheRefreshWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {
    override suspend fun doWork() = Result.success()
}
