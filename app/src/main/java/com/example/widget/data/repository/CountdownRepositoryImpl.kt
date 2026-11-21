package com.example.widget.data.repository

import android.content.Context
import com.example.widget.data.local.CountdownDao
import com.example.widget.data.local.UserPreferencesDataStore
import com.example.widget.data.model.CountdownEvent
import com.example.widget.data.model.DarkThemeConfig
import com.example.widget.data.model.SortOrder
import com.example.widget.data.model.UserPreferences
import com.example.widget.ui.widget.CountdownGlanceWidget
import com.example.widget.ui.widget.WidgetTickReceiver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class CountdownRepositoryImpl(
    private val countdownDao: CountdownDao,
    private val userPreferencesDataStore: UserPreferencesDataStore,
    private val appContext: Context
) : CountdownRepository {

    private fun notifyWidgets() {
        CoroutineScope(Dispatchers.Default).launch {
            try {
                CountdownGlanceWidget.updateAllWidgets(appContext)
                WidgetTickReceiver.scheduleTicks(appContext)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun getAllEvents(): Flow<List<CountdownEvent>> = countdownDao.getAllEvents()

    override fun getEventById(id: Long): Flow<CountdownEvent?> = countdownDao.getEventById(id)

    override suspend fun getEventByIdDirect(id: Long): CountdownEvent? = countdownDao.getEventByIdDirect(id)

    override fun getPinnedEvent(): Flow<CountdownEvent?> = countdownDao.getPinnedEvent()

    override suspend fun insertEvent(event: CountdownEvent): Long {
        val id = countdownDao.insertEvent(event)
        notifyWidgets()
        return id
    }

    override suspend fun updateEvent(event: CountdownEvent) {
        countdownDao.updateEvent(event)
        notifyWidgets()
    }

    override suspend fun deleteEvent(event: CountdownEvent) {
        countdownDao.deleteEvent(event)
        notifyWidgets()
    }

    override suspend fun deleteEventById(id: Long) {
        countdownDao.deleteEventById(id)
        notifyWidgets()
    }

    override suspend fun setPinnedEvent(eventId: Long) {
        countdownDao.setPinnedEvent(eventId)
        notifyWidgets()
    }

    override val userPreferences: Flow<UserPreferences> = userPreferencesDataStore.userPreferencesFlow

    override suspend fun setDarkThemeConfig(config: DarkThemeConfig) {
        userPreferencesDataStore.setDarkThemeConfig(config)
    }

    override suspend fun setActiveEventId(eventId: Long?) {
        userPreferencesDataStore.setActiveEventId(eventId)
    }

    override suspend fun setDefaultBackgroundPreset(presetId: String) {
        userPreferencesDataStore.setDefaultBackgroundPreset(presetId)
    }

    override suspend fun setDefaultTextColorHex(colorHex: String) {
        userPreferencesDataStore.setDefaultTextColorHex(colorHex)
    }

    override suspend fun setSortBy(sortOrder: SortOrder) {
        userPreferencesDataStore.setSortBy(sortOrder)
    }
}
