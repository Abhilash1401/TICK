package com.example.widget.data

import com.example.widget.data.model.CountdownEvent
import com.example.widget.data.model.DarkThemeConfig
import com.example.widget.data.model.SortOrder
import com.example.widget.data.model.UserPreferences
import com.example.widget.data.repository.CountdownRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class FakeCountdownRepository : CountdownRepository {

    private val eventsMap = mutableMapOf<Long, CountdownEvent>()
    private val eventsFlow = MutableStateFlow<List<CountdownEvent>>(emptyList())
    private val userPrefsFlow = MutableStateFlow(UserPreferences())
    private var nextId = 1L

    private fun emitEvents() {
        eventsFlow.value = eventsMap.values.toList()
    }

    override fun getAllEvents(): Flow<List<CountdownEvent>> = eventsFlow

    override fun getEventById(id: Long): Flow<CountdownEvent?> = eventsFlow.map { list ->
        list.find { it.id == id }
    }

    override suspend fun getEventByIdDirect(id: Long): CountdownEvent? {
        return eventsMap[id]
    }

    override fun getPinnedEvent(): Flow<CountdownEvent?> = eventsFlow.map { list ->
        list.find { it.isPinned }
    }

    override suspend fun insertEvent(event: CountdownEvent): Long {
        val id = if (event.id == 0L) nextId++ else event.id
        val newEvent = event.copy(id = id)
        eventsMap[id] = newEvent
        emitEvents()
        return id
    }

    override suspend fun updateEvent(event: CountdownEvent) {
        eventsMap[event.id] = event
        emitEvents()
    }

    override suspend fun deleteEvent(event: CountdownEvent) {
        eventsMap.remove(event.id)
        emitEvents()
    }

    override suspend fun deleteEventById(id: Long) {
        eventsMap.remove(id)
        emitEvents()
    }

    override suspend fun setPinnedEvent(eventId: Long) {
        eventsMap.keys.forEach { key ->
            val existing = eventsMap[key]!!
            eventsMap[key] = existing.copy(isPinned = key == eventId)
        }
        emitEvents()
    }

    override val userPreferences: Flow<UserPreferences> = userPrefsFlow

    override suspend fun setDarkThemeConfig(config: DarkThemeConfig) {
        userPrefsFlow.value = userPrefsFlow.value.copy(darkThemeConfig = config)
    }

    override suspend fun setActiveEventId(eventId: Long?) {
        userPrefsFlow.value = userPrefsFlow.value.copy(activeEventId = eventId)
    }

    override suspend fun setDefaultBackgroundPreset(presetId: String) {
        userPrefsFlow.value = userPrefsFlow.value.copy(defaultBackgroundPresetId = presetId)
    }

    override suspend fun setDefaultTextColorHex(colorHex: String) {
        userPrefsFlow.value = userPrefsFlow.value.copy(defaultTextColorHex = colorHex)
    }

    override suspend fun setSortBy(sortOrder: SortOrder) {
        userPrefsFlow.value = userPrefsFlow.value.copy(sortBy = sortOrder)
    }
}
