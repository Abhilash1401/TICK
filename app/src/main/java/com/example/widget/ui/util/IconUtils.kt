package com.example.widget.ui.util

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Cake
import androidx.compose.material.icons.rounded.CardGiftcard
import androidx.compose.material.icons.rounded.Celebration
import androidx.compose.material.icons.rounded.Event
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FitnessCenter
import androidx.compose.material.icons.rounded.Flight
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.Work
import androidx.compose.ui.graphics.vector.ImageVector

object IconUtils {

    fun getIconForName(name: String): ImageVector {
        return when (name.lowercase()) {
            "star" -> Icons.Rounded.Star
            "cake" -> Icons.Rounded.Cake
            "flight" -> Icons.Rounded.Flight
            "heart" -> Icons.Rounded.Favorite
            "work" -> Icons.Rounded.Work
            "fitness" -> Icons.Rounded.FitnessCenter
            "music" -> Icons.Rounded.MusicNote
            "gift" -> Icons.Rounded.CardGiftcard
            "party" -> Icons.Rounded.Celebration
            else -> Icons.Rounded.Event
        }
    }
}
