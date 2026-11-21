package com.example.widget.ui.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class TimeUtilsTest {

    @Test
    fun calculateTimeRemaining_futureEvent_returnsCorrectValues() {
        val now = 1000000000000L
        val target = now + (2 * 24 * 3600 * 1000L) + (3 * 3600 * 1000L) + (15 * 60 * 1000L) + (45 * 1000L)

        val result = TimeUtils.calculateTimeRemaining(
            targetTimestampMillis = target,
            currentTimestampMillis = now,
            countDirection = "COUNTDOWN",
            showMilliseconds = false
        )

        assertEquals(2L, result.days)
        assertEquals(3L, result.hours)
        assertEquals(15L, result.minutes)
        assertEquals(45L, result.seconds)
        assertFalse(result.isArrived)
        assertFalse(result.isPast)
    }

    @Test
    fun calculateTimeRemaining_arrivedEvent_returnsArrivedFlag() {
        val now = 1000000000000L
        val target = now - 5000L

        val result = TimeUtils.calculateTimeRemaining(
            targetTimestampMillis = target,
            currentTimestampMillis = now,
            countDirection = "COUNTDOWN"
        )

        assertTrue(result.isArrived)
        assertTrue(result.isPast)
        assertEquals("Event Arrived!", result.formattedMainText)
    }

    @Test
    fun calculateTimeRemaining_countup_calculatesElapsedTime() {
        val now = 1000000000000L
        val target = now - (1 * 3600 * 1000L) - (30 * 60 * 1000L)

        val result = TimeUtils.calculateTimeRemaining(
            targetTimestampMillis = target,
            currentTimestampMillis = now,
            countDirection = "COUNTUP"
        )

        assertEquals(0L, result.days)
        assertEquals(1L, result.hours)
        assertEquals(30L, result.minutes)
        assertFalse(result.isArrived)
    }
}
