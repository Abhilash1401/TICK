package com.example.widget.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.widget.data.model.CountdownEvent
import com.example.widget.data.model.SortOrder
import com.example.widget.data.repository.CountdownRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class DashboardUiState(
    val events: List<CountdownEvent> = emptyList(),
    val pinnedEvent: CountdownEvent? = null,
    val searchQuery: String = "",
    val selectedCategory: String? = null,
    val sortOrder: SortOrder = SortOrder.BY_TARGET_DATE,
    val isLoading: Boolean = true
)

class DashboardViewModel(
    private val repository: CountdownRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    private val _selectedCategory = MutableStateFlow<String?>(null)
    private val _sortOrder = MutableStateFlow(SortOrder.BY_TARGET_DATE)

    val uiState: StateFlow<DashboardUiState> = combine(
        repository.getAllEvents(),
        _searchQuery,
        _selectedCategory,
        _sortOrder
    ) { allEvents, searchQuery, category, sortOrder ->
        val pinned = allEvents.find { it.isPinned }

        val filteredEvents = allEvents.filter { event ->
            val matchesSearch = searchQuery.isBlank() ||
                    event.title.contains(searchQuery, ignoreCase = true) ||
                    event.category.contains(searchQuery, ignoreCase = true)
            val matchesCategory = category == null || category.equals("All", ignoreCase = true) ||
                    event.category.equals(category, ignoreCase = true)
            matchesSearch && matchesCategory
        }

        val sortedEvents = when (sortOrder) {
            SortOrder.BY_TARGET_DATE -> filteredEvents.sortedBy { it.targetTimestampMillis }
            SortOrder.BY_TITLE -> filteredEvents.sortedBy { it.title.lowercase() }
            SortOrder.BY_CREATED_DATE -> filteredEvents.sortedByDescending { it.createdAtTimestampMillis }
        }

        DashboardUiState(
            events = sortedEvents,
            pinnedEvent = pinned,
            searchQuery = searchQuery,
            selectedCategory = category,
            sortOrder = sortOrder,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = DashboardUiState()
    )

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun onCategorySelect(category: String?) {
        _selectedCategory.value = category
    }

    fun onSortOrderChange(sortOrder: SortOrder) {
        _sortOrder.value = sortOrder
        viewModelScope.launch {
            repository.setSortBy(sortOrder)
        }
    }

    fun onDeleteEvent(event: CountdownEvent) {
        viewModelScope.launch {
            repository.deleteEvent(event)
        }
    }

    fun onTogglePin(event: CountdownEvent) {
        viewModelScope.launch {
            if (event.isPinned) {
                // Unpin by updating event
                repository.updateEvent(event.copy(isPinned = false))
            } else {
                // Pin this event (and unpin others via repo setPinnedEvent)
                repository.setPinnedEvent(event.id)
            }
        }
    }
}
