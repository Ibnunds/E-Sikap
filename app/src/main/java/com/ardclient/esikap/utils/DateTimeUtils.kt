package com.ardclient.esikap.utils

import android.os.Build
import androidx.annotation.RequiresApi
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

object DateTimeUtils {
    fun formatDate(date: Long, pattern: String = "dd-MM-yyyy"): String {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = date
        val dateFormat = SimpleDateFormat(pattern, Locale.getDefault())

        return dateFormat.format(calendar.time)
    }

    fun formatTime(h: Int, m: Int): String {
        var fHour = if (h<10) "0${h}" else h
        var fMinute = if (m<10) "0${m}" else m

        return "$fHour.$fMinute"
    }

    fun formatDateTime(input: String): String {
        // Define the input and output date formats
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())

        // Set the input format's timezone to UTC
        inputFormat.timeZone = TimeZone.getTimeZone("UTC")

        // Parse the input date string to a Date object
        val date: Date? = inputFormat.parse(input)

        // Format the Date object to the desired output string
        return if (date != null) {
            outputFormat.format(date)
        } else {
            ""
        }
    }
}