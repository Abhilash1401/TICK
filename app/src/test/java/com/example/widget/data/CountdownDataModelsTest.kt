package com.example.widget.data

import com.example.widget.data.model.CountdownEvent
import com.example.widget.data.model.DarkThemeConfig
import com.example.widget.data.model.SortOrder
import com.example.widget.data.model.StylePresets
import com.example.widget.data.model.UserPreferences
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class CountdownDataModelsTest {

    @Test
    fun testCountdownEventCreation() {
        val targetTime = System.currentTimeMillis() + 86400000L
        val event = CountdownEvent(
            id = 1L,
            title = "New Year 2026",
            targetTimestampMillis = targetTime,
            category = "Holiday",
            backgroundPresetId = "gradient_midnight",
            textColorHex = "#FFD700",
            fontStyle = "SERIF",
            iconName = "Star",
            isPinned = true,
            backgroundImageUri = "content://media/external/images/media/123"
        )

        assertEquals(1L, event.id)
        assertEquals("New Year 2026", event.title)
        assertEquals(targetTime, event.targetTimestampMillis)
        assertEquals("Holiday", event.category)
        assertEquals("gradient_midnight", event.backgroundPresetId)
        assertEquals("#FFD700", event.textColorHex)
        assertEquals("SERIF", event.fontStyle)
        assertEquals("Star", event.iconName)
        assertTrue(event.isPinned)
        assertEquals("content://media/external/images/media/123", event.backgroundImageUri)
    }

    @Test
    fun testStylePresets() {
        val presets = StylePresets.backgroundPresets
        assertTrue(presets.isNotEmpty())

        val sunsetPreset = StylePresets.getBackgroundPreset("gradient_sunset")
        assertEquals("Sunset Glow", sunsetPreset.name)
        assertEquals("#FF512F", sunsetPreset.startColorHex)
        assertEquals("#DD2476", sunsetPreset.endColorHex)

        val unknownPreset = StylePresets.getBackgroundPreset("non_existent_preset")
        assertNotNull(unknownPreset)
        assertEquals("gradient_sunset", unknownPreset.id)

        assertTrue(StylePresets.textColorOptions.isNotEmpty())
        assertTrue(StylePresets.categoryList.contains("Birthday"))
        assertTrue(StylePresets.iconOptions.contains("Cake"))
    }

    @Test
    fun testUserPreferencesDefaults() {
        val prefs = UserPreferences()
        assertEquals(DarkThemeConfig.FOLLOW_SYSTEM, prefs.darkThemeConfig)
        assertEquals("gradient_sunset", prefs.defaultBackgroundPresetId)
        assertEquals("#FFFFFF", prefs.defaultTextColorHex)
        assertEquals(SortOrder.BY_TARGET_DATE, prefs.sortBy)
    }
}
