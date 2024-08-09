package com.example.googlefit

import android.app.Application
import android.util.Log
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

class HealthApp : Application() {

    override fun onCreate() {
        super.onCreate()
        Log.d("HealthApp", "onCreate called")

//        HealthDataWorker.scheduleWork(this)
        val workRequest = OneTimeWorkRequestBuilder<HealthWorker>()
            .setInitialDelay(10, TimeUnit.SECONDS)
            .build()

        WorkManager.getInstance(applicationContext).enqueue(workRequest)

    }
}
