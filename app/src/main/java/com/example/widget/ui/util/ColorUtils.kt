package com.example.widget.ui.util

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import com.example.widget.data.model.StylePresets

object ColorUtils {

    fun parseHexColor(hex: String, fallback: Color = Color.White): Color {
        return try {
            val cleanHex = hex.removePrefix("#").trim()
            val colorInt = when (cleanHex.length) {
                6 -> (0xFF000000 or cleanHex.toLong(16)).toInt()
                8 -> cleanHex.toLong(16).toInt()
                else -> return fallback
            }
            Color(colorInt)
        } catch (e: Exception) {
            fallback
        }
    }

    fun getBrushForEvent(
        backgroundPresetId: String,
        customBackgroundColorHex: String?
    ): Brush {
        if (!customBackgroundColorHex.isNullOrBlank()) {
            val color = parseHexColor(customBackgroundColorHex, Color(0xFF1E1E1E))
            return SolidColor(color)
        }

        val preset = StylePresets.getBackgroundPreset(backgroundPresetId)
        val startColor = parseHexColor(preset.startColorHex, Color(0xFFFF512F))
        val endColor = parseHexColor(preset.endColorHex, Color(0xFFDD2476))

        return if (preset.isGradient) {
            Brush.linearGradient(colors = listOf(startColor, endColor))
        } else {
            SolidColor(startColor)
        }
    }

    fun getPrimaryColorForPreset(
        backgroundPresetId: String,
        customBackgroundColorHex: String?
    ): Color {
        if (!customBackgroundColorHex.isNullOrBlank()) {
            return parseHexColor(customBackgroundColorHex, Color(0xFF1E1E1E))
        }
        val preset = StylePresets.getBackgroundPreset(backgroundPresetId)
        return parseHexColor(preset.startColorHex, Color(0xFFFF512F))
    }
}
