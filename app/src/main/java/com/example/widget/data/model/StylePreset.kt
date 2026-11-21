package com.example.widget.data.model

data class BackgroundPreset(
    val id: String,
    val name: String,
    val startColorHex: String,
    val endColorHex: String,
    val isGradient: Boolean = true
)

data class TextColorOption(
    val name: String,
    val hex: String
)

object StylePresets {
    val backgroundPresets = listOf(
        BackgroundPreset("gradient_sunset", "Sunset Glow", "#FF512F", "#DD2476"),
        BackgroundPreset("gradient_ocean", "Ocean Blue", "#2193b0", "#6dd5ed"),
        BackgroundPreset("gradient_midnight", "Midnight Purple", "#0f0c29", "#24243e"),
        BackgroundPreset("gradient_emerald", "Emerald Forest", "#11998e", "#38ef7d"),
        BackgroundPreset("gradient_neon", "Cyberpunk Neon", "#8A2387", "#E94057"),
        BackgroundPreset("gradient_golden", "Golden Hour", "#F2994A", "#F2C94C"),
        BackgroundPreset("gradient_rose", "Rose Petal", "#FF758C", "#FF7EB3"),
        BackgroundPreset("gradient_dark", "Dark Obsidian", "#1F1C2C", "#928DAB"),
        BackgroundPreset("solid_dark", "Solid Dark", "#121212", "#121212", isGradient = false),
        BackgroundPreset("solid_deep_blue", "Deep Blue", "#1A237E", "#1A237E", isGradient = false)
    )

    val textColorOptions = listOf(
        TextColorOption("Pure White", "#FFFFFF"),
        TextColorOption("Soft Cream", "#FFFDD0"),
        TextColorOption("Charcoal Dark", "#1A1A1A"),
        TextColorOption("Electric Yellow", "#FFD700"),
        TextColorOption("Cyan Spark", "#00E5FF"),
        TextColorOption("Hot Pink", "#FF4081"),
        TextColorOption("Mint Green", "#00E676")
    )

    val categoryList = listOf("General", "Birthday", "Holiday", "Work", "Anniversary", "Travel", "Fitness", "Event")

    val iconOptions = listOf("Star", "Cake", "Flight", "Heart", "Work", "Fitness", "Music", "Gift", "Party")

    fun getBackgroundPreset(id: String): BackgroundPreset {
        return backgroundPresets.find { it.id == id } ?: backgroundPresets.first()
    }
}
