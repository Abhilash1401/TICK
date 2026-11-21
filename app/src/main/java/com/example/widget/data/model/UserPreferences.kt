package com.example.widget.data.model

data class UserPreferences(
    val darkThemeConfig: DarkThemeConfig = DarkThemeConfig.FOLLOW_SYSTEM,
    val activeEventId: Long? = null,
    val defaultBackgroundPresetId: String = "gradient_sunset",
    val defaultTextColorHex: String = "#FFFFFF",
    val sortBy: SortOrder = SortOrder.BY_TARGET_DATE
)

enum class DarkThemeConfig {
    FOLLOW_SYSTEM, LIGHT, DARK
}

enum class SortOrder {
    BY_TARGET_DATE, BY_TITLE, BY_CREATED_DATE
}
