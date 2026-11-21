package com.example.widget.ui.widget

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Shader
import android.net.Uri
import com.example.widget.data.model.CountdownEvent
import com.example.widget.data.model.StylePresets

object WidgetBackgroundUtils {

    fun parseColorSafely(hexString: String?, defaultColor: Int = Color.BLACK): Int {
        if (hexString.isNullOrBlank()) return defaultColor
        return try {
            val formatted = if (!hexString.startsWith("#")) "#$hexString" else hexString
            Color.parseColor(formatted)
        } catch (e: Exception) {
            defaultColor
        }
    }

    fun generateWidgetBackgroundBitmap(
        context: Context,
        event: CountdownEvent?,
        widthPx: Int = 600,
        heightPx: Int = 300,
        cornerRadiusPx: Float = 28f
    ): Bitmap {
        val bitmap = Bitmap.createBitmap(widthPx, heightPx, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        val uriString = event?.backgroundImageUri
        var imageLoaded = false

        if (!uriString.isNullOrBlank()) {
            try {
                val uri = Uri.parse(uriString)
                // Request persistable read permission for content URIs
                if (uri.scheme == "content") {
                    try {
                        context.contentResolver.takePersistableUriPermission(
                            uri, Intent.FLAG_GRANT_READ_URI_PERMISSION
                        )
                    } catch (e: Exception) {
                        // Ignore if permission already granted or not needed
                    }
                }
                val inputStream = try {
                    if (uri.scheme == "file") {
                        java.io.File(uri.path ?: "").inputStream()
                    } else {
                        context.contentResolver.openInputStream(uri)
                    }
                } catch (e: SecurityException) {
                    e.printStackTrace()
                    null
                }
                inputStream?.use { stream ->
                    val decoded = BitmapFactory.decodeStream(stream)
                    if (decoded != null) {
                        val srcRect = Rect(0, 0, decoded.width, decoded.height)
                        val destRect = RectF(0f, 0f, widthPx.toFloat(), heightPx.toFloat())
                        val path = Path().apply {
                            addRoundRect(destRect, cornerRadiusPx, cornerRadiusPx, Path.Direction.CW)
                        }
                        canvas.save()
                        canvas.clipPath(path)
                        canvas.drawBitmap(decoded, srcRect, destRect, Paint(Paint.FILTER_BITMAP_FLAG or Paint.ANTI_ALIAS_FLAG))
                        canvas.restore()
                        imageLoaded = true
                        decoded.recycle()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        if (!imageLoaded) {
            val presetId = event?.backgroundPresetId ?: "gradient_sunset"
            val customHex = event?.customBackgroundColorHex
            val preset = StylePresets.getBackgroundPreset(presetId)

            val startColor: Int
            val endColor: Int
            val isGradient: Boolean

            if (!customHex.isNullOrBlank()) {
                val parsedColor = parseColorSafely(customHex, Color.parseColor("#1F1C2C"))
                startColor = parsedColor
                endColor = parsedColor
                isGradient = false
            } else {
                startColor = parseColorSafely(preset.startColorHex, Color.parseColor("#FF512F"))
                endColor = parseColorSafely(preset.endColorHex, Color.parseColor("#DD2476"))
                isGradient = preset.isGradient
            }

            val paint = Paint(Paint.ANTI_ALIAS_FLAG)
            if (isGradient && startColor != endColor) {
                paint.shader = LinearGradient(
                    0f, 0f,
                    widthPx.toFloat(), heightPx.toFloat(),
                    startColor, endColor,
                    Shader.TileMode.CLAMP
                )
            } else {
                paint.color = startColor
            }

            val rect = RectF(0f, 0f, widthPx.toFloat(), heightPx.toFloat())
            canvas.drawRoundRect(rect, cornerRadiusPx, cornerRadiusPx, paint)
        }

        // Draw a translucent dark overlay for text readability
        val overlayPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#40000000") // 25% black overlay
        }
        val rect = RectF(0f, 0f, widthPx.toFloat(), heightPx.toFloat())
        canvas.drawRoundRect(rect, cornerRadiusPx, cornerRadiusPx, overlayPaint)

        return bitmap
    }
}
