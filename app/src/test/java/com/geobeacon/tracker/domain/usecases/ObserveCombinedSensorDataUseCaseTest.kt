package com.geobeacon.tracker.domain.usecases

import app.cash.turbine.test
import com.geobeacon.tracker.domain.models.BleDevice
import com.geobeacon.tracker.domain.models.GeoPoint
import com.geobeacon.tracker.domain.repositories.DeviceScanner
import com.geobeacon.tracker.domain.repositories.LocationTracker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ObserveCombinedSensorDataUseCaseTest {

    private class FakeLocationTracker(private val locationFlow: Flow<GeoPoint>) : LocationTracker {
        override fun getLocationFlow(): Flow<GeoPoint> = locationFlow
    }

    private class FakeDeviceScanner(private val deviceFlow: Flow<BleDevice>) : DeviceScanner {
        override fun scanForDevices(): Flow<BleDevice> = deviceFlow
    }

    @Test
    fun `test combine flow emits correct sensor events`() = runTest {
        val testDispatcher = StandardTestDispatcher(testScheduler)
        val testScope = CoroutineScope(testDispatcher)
        val locationTracker = FakeLocationTracker(
            flow { emit(GeoPoint(-2.0, -79.0)) }
        )
        val deviceScanner = FakeDeviceScanner(
            flow { emit(BleDevice("AA:BB:CC:DD:EE:FF", -70, 1000L)) }
        )

        val useCase = ObserveCombinedSensorDataUseCase(locationTracker, deviceScanner, testScope)

        useCase().test {
            val item = awaitItem()
            assertEquals("AA:BB:CC:DD:EE:FF", item.macAddress)
            assertEquals(-70, item.rssi)
            assertEquals(-2.0, item.latitude, 0.001)
            assertEquals(-79.0, item.longitude, 0.001)
            awaitComplete()
        }
    }
}
