package com.geobeacon.tracker.presentation

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.geobeacon.tracker.domain.models.SensorEvent
import com.geobeacon.tracker.domain.usecases.GetLastEventsUseCase
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TrackingViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()
    private val getLastEventsUseCase: GetLastEventsUseCase = mockk()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `test initial state and events update`() = runTest {
        val events = listOf(
            SensorEvent(1L, "AA:BB:CC:DD:EE:FF", -60, -2.0, -79.0, 1000L, false)
        )
        coEvery { getLastEventsUseCase() } returns flowOf(events)

        val viewModel = TrackingViewModel(getLastEventsUseCase)

        viewModel.uiState.test {
            val initial = awaitItem()
            assertEquals(false, initial.isTracking)
            val updated = awaitItem()
            assertEquals(events, updated.lastEvents)
        }
    }
}
