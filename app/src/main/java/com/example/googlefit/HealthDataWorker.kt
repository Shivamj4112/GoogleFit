package com.example.googlefit

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import java.util.concurrent.TimeUnit

class HealthDataWorker(
    context: Context,
    workerParams: WorkerParameters
) : Worker(context, workerParams) {

    @SuppressLint("SuspiciousIndentation")
    override fun doWork(): Result {

        val intent = Intent(applicationContext, HealthService::class.java)
        applicationContext.startForegroundService(intent)

        return Result.success()
    }

    companion object {
        fun scheduleWork(context: Context) {
            val workRequest = OneTimeWorkRequestBuilder<HealthDataWorker>()
                .setInitialDelay(1, TimeUnit.SECONDS)
                .build()
            WorkManager.getInstance(context).enqueue(workRequest)
        }
    }
}