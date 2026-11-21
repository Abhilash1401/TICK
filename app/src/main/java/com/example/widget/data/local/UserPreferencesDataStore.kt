package com.example.widget.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.widget.data.model.DarkThemeConfig
import com.example.widget.data.model.SortOrder
import com.example.widget.data.model.UserPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

class UserPreferencesDataStore(private val context: Context) {

    private object PreferenceKeys {
        val DARK_THEME_CONFIG = stringPreferencesKey("dark_theme_config")
        val ACTIVE_EVENT_ID = longPreferencesKey("active_event_id")
        val DEFAULT_BG_PRESET = stringPreferencesKey("default_bg_preset")
        val DEFAULT_TEXT_COLOR = stringPreferencesKey("default_text_color")
        val SORT_BY = stringPreferencesKey("sort_by")
    }

    val userPreferencesFlow: Flow<UserPreferences> = context.dataStore.data.map { preferences ->
        val darkTheme = try {
            DarkThemeConfig.valueOf(
                preferences[PreferenceKeys.DARK_THEME_CONFIG] ?: DarkThemeConfig.FOLLOW_SYSTEM.name
            )
        } catch (e: Exception) {
            DarkThemeConfig.FOLLOW_SYSTEM
        }

        val activeEventId = preferences[PreferenceKeys.ACTIVE_EVENT_ID]
        val bgPreset = preferences[PreferenceKeys.DEFAULT_BG_PRESET] ?: "gradient_sunset"
        val textColor = preferences[PreferenceKeys.DEFAULT_TEXT_COLOR] ?: "#FFFFFF"

        val sortBy = try {
            SortOrder.valueOf(
                preferences[PreferenceKeys.SORT_BY] ?: SortOrder.BY_TARGET_DATE.name
            )
        } catch (e: Exception) {
            SortOrder.BY_TARGET_DATE
        }

        UserPreferences(
            darkThemeConfig = darkTheme,
            activeEventId = activeEventId,
            defaultBackgroundPresetId = bgPreset,
            defaultTextColorHex = textColor,
            sortBy = sortBy
        )
    }

    suspend fun setDarkThemeConfig(config: DarkThemeConfig) {
        context.dataStore.edit { preferences ->
            preferences[PreferenceKeys.DARK_THEME_CONFIG] = config.name
        }
    }

    suspend fun setActiveEventId(eventId: Long?) {
        context.dataStore.edit { preferences ->
            if (eventId != null) {
                preferences[PreferenceKeys.ACTIVE_EVENT_ID] = eventId
            } else {
                preferences.remove(PreferenceKeys.ACTIVE_EVENT_ID)
            }
        }
    }

    suspend fun setDefaultBackgroundPreset(presetId: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferenceKeys.DEFAULT_BG_PRESET] = presetId
        }
    }

    suspend fun setDefaultTextColorHex(colorHex: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferenceKeys.DEFAULT_TEXT_COLOR] = colorHex
        }
    }

    suspend fun setSortBy(sortOrder: SortOrder) {
        context.dataStore.edit { preferences ->
            preferences[PreferenceKeys.SORT_BY] = sortOrder.name
        }
    }
}
