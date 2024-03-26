package com.ardclient.esikap.utils

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

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
}