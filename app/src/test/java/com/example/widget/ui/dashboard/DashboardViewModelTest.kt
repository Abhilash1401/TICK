package com.example.widget.ui.dashboard

import com.example.widget.data.FakeCountdownRepository
import com.example.widget.data.model.CountdownEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DashboardViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: FakeCountdownRepository
    private lateinit var viewModel: DashboardViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        repository = FakeCountdownRepository()
        viewModel = DashboardViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun eventsList_initiallyEmpty() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiState.collect {} }
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state.events.isEmpty())
        assertNull(state.pinnedEvent)
    }

    @Test
    fun insertEvents_filtersBySearchQuery() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiState.collect {} }

        val event1 = CountdownEvent(id = 1, title = "Birthday Party", category = "Birthday", targetTimestampMillis = 2000)
        val event2 = CountdownEvent(id = 2, title = "Vacation Trip", category = "Travel", targetTimestampMillis = 3000)
        repository.insertEvent(event1)
        repository.insertEvent(event2)

        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.onSearchQueryChange("Party")
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(1, state.events.size)
        assertEquals("Birthday Party", state.events.first().title)
    }

    @Test
    fun insertEvents_filtersByCategory() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiState.collect {} }

        val event1 = CountdownEvent(id = 1, title = "Birthday Party", category = "Birthday", targetTimestampMillis = 2000)
        val event2 = CountdownEvent(id = 2, title = "Vacation Trip", category = "Travel", targetTimestampMillis = 3000)
        repository.insertEvent(event1)
        repository.insertEvent(event2)

        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.onCategorySelect("Travel")
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(1, state.events.size)
        assertEquals("Vacation Trip", state.events.first().title)
    }

    @Test
    fun togglePin_pinsSelectedEvent() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiState.collect {} }

        val event1 = CountdownEvent(id = 1, title = "Event 1", targetTimestampMillis = 1000, isPinned = false)
        val event2 = CountdownEvent(id = 2, title = "Event 2", targetTimestampMillis = 2000, isPinned = false)
        repository.insertEvent(event1)
        repository.insertEvent(event2)

        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.onTogglePin(event2)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertNotNull(state.pinnedEvent)
        assertEquals(2L, state.pinnedEvent?.id)
    }

    @Test
    fun deleteEvent_removesEventFromState() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiState.collect {} }

        val event = CountdownEvent(id = 1, title = "To Delete", targetTimestampMillis = 1000)
        repository.insertEvent(event)

        testDispatcher.scheduler.advanceUntilIdle()
        assertEquals(1, viewModel.uiState.value.events.size)

        viewModel.onDeleteEvent(event)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(0, viewModel.uiState.value.events.size)
    }
}
