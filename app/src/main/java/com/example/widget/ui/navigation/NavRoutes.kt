package com.example.widget.ui.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

sealed interface AppRoute : NavKey {
    @Serializable
    data object Dashboard : AppRoute

    @Serializable
    data class CountdownConfig(val eventId: Long? = null) : AppRoute
}
