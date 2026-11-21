package com.example.widget.ui.config

import com.example.widget.data.FakeCountdownRepository
import com.example.widget.data.model.CountdownEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
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
class CountdownConfigViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: FakeCountdownRepository
    private lateinit var viewModel: CountdownConfigViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        repository = FakeCountdownRepository()
        viewModel = CountdownConfigViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun loadEvent_nullId_initializesNewEventDefaults() = runTest {
        viewModel.loadEvent(null)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertNull(state.eventId)
        assertEquals("", state.title)
        assertEquals("General", state.category)
        assertEquals("gradient_sunset", state.backgroundPresetId)
    }

    @Test
    fun loadEvent_existingId_populatesFormFields() = runTest {
        val existing = CountdownEvent(
            id = 5,
            title = "Anniversary Celebration",
            category = "Anniversary",
            targetTimestampMillis = 1750000000000L,
            backgroundPresetId = "gradient_ocean",
            textColorHex = "#FFD700",
            iconName = "Heart"
        )
        repository.insertEvent(existing)

        viewModel.loadEvent(5)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(5L, state.eventId)
        assertEquals("Anniversary Celebration", state.title)
        assertEquals("Anniversary", state.category)
        assertEquals("gradient_ocean", state.backgroundPresetId)
        assertEquals("#FFD700", state.textColorHex)
        assertEquals("Heart", state.iconName)
    }

    @Test
    fun saveEvent_emptyTitle_showsError() = runTest {
        viewModel.loadEvent(null)
        testDispatcher.scheduler.advanceUntilIdle()

        var successCalled = false
        viewModel.saveEvent { successCalled = true }
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertNotNull(state.errorMessage)
        assertEquals("Please enter a title for the event", state.errorMessage)
        assertTrue(!successCalled)
    }

    @Test
    fun saveEvent_validData_insertsEventAndNavigates() = runTest {
        viewModel.loadEvent(null)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.onTitleChange("New Year Party")
        viewModel.onCategoryChange("Holiday")
        viewModel.onBackgroundPresetChange("gradient_neon")

        var successCalled = false
        viewModel.saveEvent { successCalled = true }
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state.saveSuccess)
        assertTrue(successCalled)

        val insertedEvent = repository.getEventByIdDirect(1)
        assertNotNull(insertedEvent)
        assertEquals("New Year Party", insertedEvent?.title)
        assertEquals("Holiday", insertedEvent?.category)
        assertEquals("gradient_neon", insertedEvent?.backgroundPresetId)
    }

    @Test
    fun saveEvent_withBackgroundImage_savesUri() = runTest {
        viewModel.loadEvent(null)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.onTitleChange("Vacation")
        viewModel.onBackgroundImageChange("content://media/external/images/media/456")

        var successCalled = false
        viewModel.saveEvent { successCalled = true }
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(successCalled)
        val saved = repository.getEventByIdDirect(1)
        assertNotNull(saved)
        assertEquals("content://media/external/images/media/456", saved?.backgroundImageUri)
    }
}
