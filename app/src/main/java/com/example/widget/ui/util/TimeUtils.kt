package com.example.widget.ui.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.abs

data class TimeRemaining(
    val days: Long,
    val hours: Long,
    val minutes: Long,
    val seconds: Long,
    val millis: Long,
    val isPast: Boolean,
    val isArrived: Boolean,
    val formattedMainText: String,
    val formattedSubText: String
)

object TimeUtils {

    fun calculateTimeRemaining(
        targetTimestampMillis: Long,
        currentTimestampMillis: Long = System.currentTimeMillis(),
        countDirection: String = "COUNTDOWN",
        showMilliseconds: Boolean = false
    ): TimeRemaining {
        val rawDiff = if (countDirection == "COUNTUP") {
            currentTimestampMillis - targetTimestampMillis
        } else {
            targetTimestampMillis - currentTimestampMillis
        }

        val isArrived = rawDiff <= 0 && countDirection == "COUNTDOWN"
        val isPast = currentTimestampMillis > targetTimestampMillis
        val diff = if (rawDiff < 0) 0L else rawDiff

        val totalSeconds = diff / 1000
        val days = totalSeconds / (24 * 3600)
        val hours = (totalSeconds % (24 * 3600)) / 3600
        val minutes = (totalSeconds % 3600) / 60
        val seconds = totalSeconds % 60
        val millis = (diff % 1000) / 10

        val daysStr = days.toString().padStart(2, '0')
        val hoursStr = hours.toString().padStart(2, '0')
        val minutesStr = minutes.toString().padStart(2, '0')
        val secondsStr = seconds.toString().padStart(2, '0')
        val millisStr = millis.toString().padStart(2, '0')

        val formattedMainText: String
        val formattedSubText: String

        if (isArrived && countDirection == "COUNTDOWN") {
            formattedMainText = "Event Arrived!"
            formattedSubText = "00d 00h 00m 00s"
        } else {
            formattedMainText = if (days > 0) {
                "${days}d ${hoursStr}h ${minutesStr}m"
            } else {
                "${hoursStr}h ${minutesStr}m ${secondsStr}s"
            }

            formattedSubText = if (showMilliseconds) {
                "${daysStr}d ${hoursStr}h ${minutesStr}m ${secondsStr}s .${millisStr}"
            } else {
                "${daysStr}d ${hoursStr}h ${minutesStr}m ${secondsStr}s"
            }
        }

        return TimeRemaining(
            days = days,
            hours = hours,
            minutes = minutes,
            seconds = seconds,
            millis = millis,
            isPast = isPast,
            isArrived = isArrived,
            formattedMainText = formattedMainText,
            formattedSubText = formattedSubText
        )
    }

    fun formatDateTime(timestampMillis: Long): String {
        val sdf = SimpleDateFormat("MMM dd, yyyy 'at' hh:mm a", Locale.getDefault())
        return sdf.format(Date(timestampMillis))
    }

    fun formatDateOnly(timestampMillis: Long): String {
        val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        return sdf.format(Date(timestampMillis))
    }

    fun formatTimeOnly(timestampMillis: Long): String {
        val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
        return sdf.format(Date(timestampMillis))
    }
}
