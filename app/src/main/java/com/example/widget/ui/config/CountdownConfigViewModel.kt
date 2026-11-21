package com.example.widget.ui.config

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.widget.data.model.CountdownEvent
import com.example.widget.data.repository.CountdownRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar

data class CountdownConfigUiState(
    val eventId: Long? = null,
    val title: String = "",
    val targetTimestampMillis: Long = System.currentTimeMillis() + 86400000L, // +1 day default
    val category: String = "General",
    val backgroundPresetId: String = "gradient_sunset",
    val customBackgroundColorHex: String? = null,
    val textColorHex: String = "#FFFFFF",
    val fontStyle: String = "DEFAULT",
    val iconName: String = "Star",
    val isPinned: Boolean = false,
    val showMilliseconds: Boolean = false,
    val countDirection: String = "COUNTDOWN",
    val repeatInterval: String = "NONE",
    val backgroundImageUri: String? = null,
    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false,
    val errorMessage: String? = null
)

class CountdownConfigViewModel(
    private val repository: CountdownRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CountdownConfigUiState())
    val uiState: StateFlow<CountdownConfigUiState> = _uiState.asStateFlow()

    fun loadEvent(eventId: Long?) {
        if (eventId == null || eventId == 0L) {
            val tomorrow = Calendar.getInstance().apply {
                add(Calendar.DAY_OF_YEAR, 1)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.timeInMillis

            _uiState.value = CountdownConfigUiState(
                eventId = null,
                targetTimestampMillis = tomorrow
            )
        } else {
            viewModelScope.launch {
                val event = repository.getEventByIdDirect(eventId)
                if (event != null) {
                    _uiState.value = CountdownConfigUiState(
                        eventId = event.id,
                        title = event.title,
                        targetTimestampMillis = event.targetTimestampMillis,
                        category = event.category,
                        backgroundPresetId = event.backgroundPresetId,
                        customBackgroundColorHex = event.customBackgroundColorHex,
                        textColorHex = event.textColorHex,
                        fontStyle = event.fontStyle,
                        iconName = event.iconName,
                        isPinned = event.isPinned,
                        showMilliseconds = event.showMilliseconds,
                        countDirection = event.countDirection,
                        repeatInterval = event.repeatInterval,
                        backgroundImageUri = event.backgroundImageUri
                    )
                }
            }
        }
    }

    fun onTitleChange(title: String) {
        _uiState.update { it.copy(title = title, errorMessage = null) }
    }

    fun onCategoryChange(category: String) {
        _uiState.update { it.copy(category = category) }
    }

    fun onTargetTimestampChange(timestampMillis: Long) {
        _uiState.update { it.copy(targetTimestampMillis = timestampMillis) }
    }

    fun onBackgroundPresetChange(presetId: String) {
        _uiState.update { it.copy(backgroundPresetId = presetId, customBackgroundColorHex = null) }
    }

    fun onCustomColorChange(hex: String?) {
        _uiState.update { it.copy(customBackgroundColorHex = hex) }
    }

    fun onTextColorChange(hex: String) {
        _uiState.update { it.copy(textColorHex = hex) }
    }

    fun onIconChange(iconName: String) {
        _uiState.update { it.copy(iconName = iconName) }
    }

    fun onShowMillisecondsChange(show: Boolean) {
        _uiState.update { it.copy(showMilliseconds = show) }
    }

    fun onCountDirectionChange(direction: String) {
        _uiState.update { it.copy(countDirection = direction) }
    }

    fun onRepeatIntervalChange(interval: String) {
        _uiState.update { it.copy(repeatInterval = interval) }
    }

    fun onBackgroundImageChange(uri: String?) {
        _uiState.update { it.copy(backgroundImageUri = uri) }
    }

    fun saveEvent(onSuccess: () -> Unit) {
        val currentState = _uiState.value
        if (currentState.title.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Please enter a title for the event") }
            return
        }

        _uiState.update { it.copy(isSaving = true) }

        viewModelScope.launch {
            try {
                val eventToSave = CountdownEvent(
                    id = currentState.eventId ?: 0L,
                    title = currentState.title.trim(),
                    targetTimestampMillis = currentState.targetTimestampMillis,
                    category = currentState.category,
                    backgroundPresetId = currentState.backgroundPresetId,
                    customBackgroundColorHex = currentState.customBackgroundColorHex,
                    textColorHex = currentState.textColorHex,
                    fontStyle = currentState.fontStyle,
                    iconName = currentState.iconName,
                    isPinned = currentState.isPinned,
                    showMilliseconds = currentState.showMilliseconds,
                    countDirection = currentState.countDirection,
                    repeatInterval = currentState.repeatInterval,
                    backgroundImageUri = currentState.backgroundImageUri
                )

                val savedId = if (currentState.eventId == null || currentState.eventId == 0L) {
                    repository.insertEvent(eventToSave)
                } else {
                    repository.updateEvent(eventToSave)
                    currentState.eventId
                }

                repository.setActiveEventId(savedId)

                _uiState.update { it.copy(isSaving = false, saveSuccess = true) }
                onSuccess()
            } catch (e: Exception) {
                _uiState.update { it.copy(isSaving = false, errorMessage = e.localizedMessage ?: "Failed to save event") }
            }
        }
    }
}
