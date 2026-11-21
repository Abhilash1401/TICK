package com.example.widget.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "countdown_events")
data class CountdownEvent(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val targetTimestampMillis: Long,
    val createdAtTimestampMillis: Long = System.currentTimeMillis(),
    val category: String = "General",
    val backgroundPresetId: String = "gradient_sunset",
    val customBackgroundColorHex: String? = null,
    val textColorHex: String = "#FFFFFF",
    val fontStyle: String = "DEFAULT",
    val iconName: String = "Star",
    val isPinned: Boolean = false,
    val showMilliseconds: Boolean = false,
    val countDirection: String = "COUNTDOWN", // COUNTDOWN or COUNTUP
    val repeatInterval: String = "NONE", // NONE, DAILY, WEEKLY, MONTHLY, YEARLY
    val backgroundImageUri: String? = null
)
