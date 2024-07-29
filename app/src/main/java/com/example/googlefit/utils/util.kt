package com.example.googlefit.utils

import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

object util {

    fun formatLastModifiedTime(lastModifiedTime: String, is24HourFormat: Boolean = false): String {
        val offsetDateTime = OffsetDateTime.parse(lastModifiedTime)
        val istOffsetDateTime = offsetDateTime.withOffsetSameInstant(ZoneOffset.ofHoursMinutes(5, 30))
        val timeFormatter = if (is24HourFormat) {
            DateTimeFormatter.ofPattern("HH:mm:ss")
        } else {
            DateTimeFormatter.ofPattern("hh:mm:ss a")
        }
        return istOffsetDateTime.format(timeFormatter)
    }
}