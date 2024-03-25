package com.ardclient.esikap.utils

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

object DateTimeUtils {
    fun formatDate(date: Long): String {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = date
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

        return dateFormat.format(calendar.time)
    }
}