package com.onewordaday.app.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

object DateUtils {
    private fun isoFormatter() = SimpleDateFormat("yyyy-MM-dd", Locale.US)

    fun today(): String = isoFormatter().format(Calendar.getInstance().time)

    fun tomorrow(): String {
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, 1)
        return isoFormatter().format(cal.time)
    }

    fun formatForDisplay(dateStr: String): String {
        return try {
            val date = isoFormatter().parse(dateStr) ?: return dateStr
            SimpleDateFormat("MMMM d, yyyy", Locale.getDefault()).format(date)
        } catch (e: Exception) {
            dateStr
        }
    }

    fun delayUntilTimeMs(hour: Int, minute: Int): Long {
        val now = Calendar.getInstance()
        val target = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        if (!target.after(now)) target.add(Calendar.DAY_OF_YEAR, 1)
        return target.timeInMillis - now.timeInMillis
    }

    fun delayUntilMidnightMs(): Long = delayUntilTimeMs(0, 5)
}
