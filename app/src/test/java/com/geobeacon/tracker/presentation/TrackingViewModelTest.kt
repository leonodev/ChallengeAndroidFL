package com.geobeacon.tracker.presentation

import app.cash.turbine.test
import com.geobeacon.tracker.domain.models.GeoPoint
import com.geobeacon.tracker.domain.models.SensorEvent
import com.geobeacon.tracker.domain.repositories.TrackingController
import com.geobeacon.tracker.domain.usecases.GetLastEventsUseCase
import com.geobeacon.tracker.domain.usecases.ObserveTrackingStateUseCase
import com.geobeacon.tracker.domain.usecases.StartTrackingUseCase
import com.geobeacon.tracker.domain.usecases.StopTrackingUseCase
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TrackingViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val getLastEventsUseCase: GetLastEventsUseCase = mockk()

    private class FakeTrackingController : TrackingController {
        private val _isTracking = MutableStateFlow(false)
        override val isTracking: StateFlow<Boolean> = _isTracking.asStateFlow()
        var startCalledCount = 0
        var stopCalledCount = 0

        override fun startTracking() {
            _isTracking.value = true
            startCalledCount++
        }

        override fun stopTracking() {
            _isTracking.value = false
            stopCalledCount++
        }
    }

    private lateinit var fakeController: FakeTrackingController

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakeController = FakeTrackingController()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `test initial state and events update`() = runTest {
        val events = listOf(
            SensorEvent(1L, "AA:BB:CC:DD:EE:FF", -60, GeoPoint(-2.0, -79.0), 1000L)
        )
        coEvery { getLastEventsUseCase() } returns flowOf(events)

        val observeTrackingStateUseCase = ObserveTrackingStateUseCase(fakeController)
        val startTrackingUseCase = StartTrackingUseCase(fakeController)
        val stopTrackingUseCase = StopTrackingUseCase(fakeController)
        val viewModel = TrackingViewModel(getLastEventsUseCase, observeTrackingStateUseCase, startTrackingUseCase, stopTrackingUseCase)

        viewModel.uiState.test {
            val initial = awaitItem()
            assertEquals(false, initial.isTracking)
            val updated = awaitItem()
            assertEquals(1, updated.lastEvents.size)
            assertEquals("AA:BB:CC:DD:EE:FF", updated.lastEvents[0].macAddress)
        }
    }

    @Test
    fun `test toggle actions trigger controller`() = runTest {
        coEvery { getLastEventsUseCase() } returns flowOf(emptyList())

        val observeTrackingStateUseCase = ObserveTrackingStateUseCase(fakeController)
        val startTrackingUseCase = StartTrackingUseCase(fakeController)
        val stopTrackingUseCase = StopTrackingUseCase(fakeController)
        val viewModel = TrackingViewModel(getLastEventsUseCase, observeTrackingStateUseCase, startTrackingUseCase, stopTrackingUseCase)

        viewModel.uiState.test {
            awaitItem() // initial

            viewModel.onAction(TrackingAction.ToggleTracking(true))
            assertEquals(1, fakeController.startCalledCount)
            assertEquals(true, awaitItem().isTracking)

            viewModel.onAction(TrackingAction.ToggleTracking(false))
            assertEquals(1, fakeController.stopCalledCount)
            assertEquals(false, awaitItem().isTracking)
        }
    }
}
