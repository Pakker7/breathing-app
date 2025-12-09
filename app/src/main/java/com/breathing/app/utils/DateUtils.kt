package com.breathing.app.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DateUtils {
    fun formatDate(dateString: String): String {
        val date = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault()).parse(dateString)
            ?: return dateString

        val today = Date()
        val yesterday = Date(today.time - 24 * 60 * 60 * 1000)

        return when {
            isSameDay(date, today) -> "오늘"
            isSameDay(date, yesterday) -> "어제"
            else -> {
                val formatter = SimpleDateFormat("M월 d일", Locale.getDefault())
                formatter.format(date)
            }
        }
    }

    fun formatTime(dateString: String): String {
        val date = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault()).parse(dateString)
            ?: return ""

        val hours = date.hours
        val mins = date.minutes
        val period = if (hours < 12) "오전" else "오후"
        val displayHours = if (hours % 12 == 0) 12 else hours % 12
        val displayMins = mins.toString().padStart(2, '0')

        return "$period $displayHours:$displayMins"
    }

    fun formatDuration(seconds: Int): String {
        val hours = seconds / 3600
        val mins = (seconds % 3600) / 60
        val secs = seconds % 60

        return when {
            hours > 0 -> "${hours}시간 ${mins}분"
            mins > 0 -> "${mins}분 ${secs}초"
            else -> "${secs}초"
        }
    }

    fun formatDurationShort(seconds: Int): String {
        val hours = seconds / 3600
        val mins = (seconds % 3600) / 60

        return when {
            hours > 0 -> "${hours}시간 ${mins}분"
            else -> "${mins}분"
        }
    }

    private fun isSameDay(date1: Date, date2: Date): Boolean {
        return date1.year == date2.year &&
                date1.month == date2.month &&
                date1.date == date2.date
    }

    fun getTodayDateString(): String {
        return SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault()).format(Date())
    }

    fun getDateString(date: Date): String {
        return date.toDateString()
    }

    private fun Date.toDateString(): String {
        return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(this)
    }
}

