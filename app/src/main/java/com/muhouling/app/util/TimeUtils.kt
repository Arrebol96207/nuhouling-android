package com.muhouling.app.util

import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

object TimeUtils {
    private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    fun isBeforeDeadline(deadline: String): Boolean {
        val now = LocalTime.now()
        val deadlineTime = LocalTime.parse(deadline, timeFormatter)
        return now.isBefore(deadlineTime)
    }

    fun isAfterWakeTime(wakeTime: String): Boolean {
        val now = LocalTime.now()
        val wake = LocalTime.parse(wakeTime, timeFormatter)
        return now.isAfter(wake)
    }

    fun getCurrentDate(): String {
        return LocalDate.now().format(dateFormatter)
    }

    fun formatTime(time: String): String {
        return try {
            val parsed = LocalTime.parse(time, timeFormatter)
            parsed.format(timeFormatter)
        } catch (e: Exception) {
            time
        }
    }
}
