package com.example.googlefit.utils

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit

object util {

    fun formatLastModifiedTime(lastModifiedTime: String, is24HourFormat: Boolean = false): String {
        val offsetDateTime = OffsetDateTime.parse(lastModifiedTime)
        val istOffsetDateTime = offsetDateTime.withOffsetSameInstant(ZoneOffset.ofHoursMinutes(5, 30))
        val timeFormatter = if (is24HourFormat) {
            DateTimeFormatter.ofPattern("HH:mm")
        } else {
            DateTimeFormatter.ofPattern("hh:mm a")
        }
        return istOffsetDateTime.format(timeFormatter)
    }

    fun formateDate(date: String): String {
        return OffsetDateTime.parse(date).format(DateTimeFormatter.ofPattern("MMMM dd"))
    }

    fun getWeekday(date: String): String {
        val formatter = DateTimeFormatter.ofPattern("EEEE")
        return formatter.format(OffsetDateTime.parse(date))
    }

    fun timeDiffInSeconds(start: String, end: String): Int {
        val startTime = OffsetDateTime.parse(start)
        val endTime = OffsetDateTime.parse(end)
        return (endTime.toEpochSecond() - startTime.toEpochSecond()).toInt()
    }

    fun formatDuration(seconds: Int): String {
        val hours = seconds / 3600
        val minutes = (seconds % 3600) / 60
        return "${hours}h ${minutes}m"
    }

    suspend fun fetchInternetTime(): Long? {
        return withContext(Dispatchers.IO) {
            try {
                val client = OkHttpClient.Builder()
                    .connectTimeout(10, TimeUnit.SECONDS)
                    .readTimeout(10, TimeUnit.SECONDS)
                    .build()

                val request = Request.Builder()
                    .url("https://worldtimeapi.org/api/timezone/Asia/Kolkata")
                    .build()

                val response = client.newCall(request).execute()
                val responseData = response.body?.string()
                if (responseData != null) {
                    val json = JSONObject(responseData)
                    val datetime = json.getString("datetime")
                    val internetTime = java.time.ZonedDateTime.parse(datetime).toInstant().toEpochMilli()
                    return@withContext internetTime
                } else {
                    null
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Log.d("TAg", "fetchInternetTime: ${e.message}")
                null
            }
        }
    }

}