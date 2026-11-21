package com.example.widget.data.repository

import com.example.widget.data.model.CountdownEvent
import com.example.widget.data.model.DarkThemeConfig
import com.example.widget.data.model.SortOrder
import com.example.widget.data.model.UserPreferences
import kotlinx.coroutines.flow.Flow

interface CountdownRepository {
    fun getAllEvents(): Flow<List<CountdownEvent>>
    fun getEventById(id: Long): Flow<CountdownEvent?>
    suspend fun getEventByIdDirect(id: Long): CountdownEvent?
    fun getPinnedEvent(): Flow<CountdownEvent?>
    suspend fun insertEvent(event: CountdownEvent): Long
    suspend fun updateEvent(event: CountdownEvent)
    suspend fun deleteEvent(event: CountdownEvent)
    suspend fun deleteEventById(id: Long)
    suspend fun setPinnedEvent(eventId: Long)

    val userPreferences: Flow<UserPreferences>
    suspend fun setDarkThemeConfig(config: DarkThemeConfig)
    suspend fun setActiveEventId(eventId: Long?)
    suspend fun setDefaultBackgroundPreset(presetId: String)
    suspend fun setDefaultTextColorHex(colorHex: String)
    suspend fun setSortBy(sortOrder: SortOrder)
}
