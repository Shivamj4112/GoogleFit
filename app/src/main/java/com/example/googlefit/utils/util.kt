package com.example.googlefit.utils

import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

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

}