package com.example.googlefit

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleOwner
import androidx.work.CoroutineWorker
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import java.util.concurrent.TimeUnit

class HealthWorker(
    val context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    private val ACTIVITY_CHANNEL_ID = "Activity Chanel"
    private val VITALS_CHANNEL_ID = "Vitals Chanel"
    private val NOTIFICATION_ID = 1
    private lateinit var notificationBuilder: NotificationCompat.Builder
    private val previousValues = mutableMapOf<String, Int>()
    private val vitalsValues = mutableMapOf<String, String>()
    val healthManager = HealthBackgroundManager(applicationContext)

    override suspend fun doWork(): Result {
        createNotificationChannels()

        val lifecycleOwner = context as LifecycleOwner

        notificationBuilder = NotificationCompat.Builder(applicationContext, ACTIVITY_CHANNEL_ID).apply {
            setContentTitle("Activity Monitor")
            setSmallIcon(R.drawable.ic_launcher_background)
        }

        notificationBuilder = NotificationCompat.Builder(applicationContext, VITALS_CHANNEL_ID).apply {
            setContentTitle("Vitals Monitor")
            setSmallIcon(R.drawable.ic_launcher_foreground)
        }


        fetchAndProcessActivityData(lifecycleOwner,healthManager)
        fetchAndProcessVitalsData(lifecycleOwner,healthManager)

        Log.d("HealthWorker", "Data fetched and notifications shown")

        val continuation = WorkManager.getInstance(applicationContext)
            .beginWith(OneTimeWorkRequestBuilder<HealthWorker>()
                .setInitialDelay(10, TimeUnit.SECONDS)
                .build())

        continuation.enqueue()

        return Result.success()
    }

    private fun createNotificationChannels() {
        val channels = listOf(
            NotificationChannel(
                ACTIVITY_CHANNEL_ID,
                "Activity Data",
                NotificationManager.IMPORTANCE_HIGH
            ),
            NotificationChannel(
                VITALS_CHANNEL_ID,
                "Vitals Data",
                NotificationManager.IMPORTANCE_HIGH
            )
        )

        val manager = applicationContext.getSystemService(NotificationManager::class.java)
        manager.createNotificationChannels(channels)
    }

    private fun showActivityNotification() {
        val inboxStyle = NotificationCompat.InboxStyle()

        previousValues.forEach { (key, value) ->
            val content = when (key) {
                "steps" -> "Steps: $value steps"
                "calories" -> "Calories: $value Cal"
                "distance" -> "Distance: $value km"
                "speed" -> "Speed: $value km/h"
                "cycling" -> "Cycling: $value rpm"
                "power" -> "Power: $value W"
                else -> null
            }
            content?.let { inboxStyle.addLine(it) }
        }

        notificationBuilder.setStyle(inboxStyle)

        val notificationManager = applicationContext.getSystemService(NotificationManager::class.java)
        notificationManager.notify(1, notificationBuilder.build())
    }

    private fun showVitalsNotification() {
        val inboxStyle = NotificationCompat.InboxStyle()
        vitalsValues.forEach { (_, content) ->
            inboxStyle.addLine(content)
        }

        notificationBuilder.setStyle(inboxStyle)

        val notificationManager = applicationContext.getSystemService(NotificationManager::class.java)
        notificationManager.notify(2, notificationBuilder.build())
    }

    private fun fetchAndProcessActivityData(
        lifecycleOwner: LifecycleOwner,
        healthManager: HealthBackgroundManager
    ) {
        healthManager.stepsRecords.observe(lifecycleOwner) { records ->
            processActivityData(
                key = "steps",
                value = records.lastOrNull()?.count?.toInt()
            )
        }

        healthManager.caloriesRecords.observe(lifecycleOwner) { records ->
            processActivityData(
                key = "calories",
                value = records.lastOrNull()?.energy?.inKilocalories?.toInt()
            )
        }

        healthManager.distanceRecords.observe(lifecycleOwner) { records ->
            processActivityData(
                key = "distance",
                value = records.lastOrNull()?.distance?.inKilometers?.toInt()
            )
        }

        healthManager.speedRecords.observe(lifecycleOwner) { records ->
            processActivityData(
                key = "speed",
                value = records.lastOrNull()?.samples?.last()?.speed?.inKilometersPerHour?.toInt()
            )
        }

        healthManager.cyclingRecords.observe(lifecycleOwner) { records ->
            processActivityData(
                key = "cycling",
                value = records.lastOrNull()?.samples?.last()?.revolutionsPerMinute?.toInt()
            )
        }

        healthManager.powerRecords.observe(lifecycleOwner) { records ->
            processActivityData(
                key = "power",
                value = records.lastOrNull()?.samples?.last()?.power?.inWatts?.toInt()
            )
        }
    }

    private fun fetchAndProcessVitalsData(
        lifecycleOwner: LifecycleOwner,
        healthManager: HealthBackgroundManager
    ) {
        healthManager.heartRateRecords.observe(lifecycleOwner) { records ->
            processVitalSign(
                key = "heartRate",
                value = records.lastOrNull()?.samples?.last()?.beatsPerMinute?.toInt(),
                normalRange = 60..100,
                label = "Heart rate",
                unit = "bpm"
            )
        }

        healthManager.bloodPressureRecords.observe(lifecycleOwner) { records ->
            val systolicRate = records.lastOrNull()?.systolic?.inMillimetersOfMercury?.toInt()
            val diastolicRate = records.lastOrNull()?.diastolic?.inMillimetersOfMercury?.toInt()

            processVitalSign(
                key = "bloodPressure",
                value = systolicRate,
                normalRange = 100..140,
                label = "Blood pressure",
                unit = "mmHg",
                extraValue = diastolicRate
            )
        }

        healthManager.bloodGlucoseRecords.observe(lifecycleOwner) { records ->
            processVitalSign(
                key = "bloodGlucose",
                value = records.lastOrNull()?.level?.inMillimolesPerLiter?.toInt(),
                normalRange = 80..120,
                label = "Blood glucose",
                unit = "mmol/L"
            )
        }

        healthManager.respiratoryRateRecords.observe(lifecycleOwner) { records ->
            processVitalSign(
                key = "respiratoryRate",
                value = records.lastOrNull()?.rate?.toInt(),
                normalRange = 12..20,
                label = "Respiratory rate",
                unit = "rpm"
            )
        }

        healthManager.bodyTemperatureRecords.observe(lifecycleOwner) { records ->
            processVitalSign(
                key = "bodyTemperature",
                value = records.lastOrNull()?.temperature?.inCelsius?.toInt(),
                normalRange = 36..37,
                label = "Body temperature",
                unit = "°C"
            )
        }

        healthManager.oxygenSaturationRecords.observe(lifecycleOwner) { records ->
            processVitalSign(
                key = "oxygenSaturation",
                value = records.lastOrNull()?.percentage?.value?.toInt(),
                normalRange = 90..100,
                label = "Oxygen saturation",
                unit = "%"
            )
        }
    }

    private fun processActivityData(key: String, value: Int?) {
        value?.let {
            if (previousValues[key] != it) {
                showActivityNotification()
                previousValues[key] = it
            }
        }
    }

    private fun processVitalSign(
        key: String,
        value: Int?,
        normalRange: IntRange,
        label: String,
        unit: String,
        extraValue: Int? = null
    ) {
        value?.let {
            if (previousValues[key] != it || (extraValue != null && previousValues["${key}_extra"] != extraValue)) {
                val status = if (it in normalRange) "Normal" else "High Risk"
                val content = if (extraValue != null) {
                    "$label is $it/$extraValue $unit ($status)"
                } else {
                    "$label is $it $unit ($status)"
                }
                vitalsValues[key] = content
                showVitalsNotification()
                previousValues[key] = it
                if (extraValue != null) previousValues["${key}_extra"] = extraValue
            }
        }
    }
}
