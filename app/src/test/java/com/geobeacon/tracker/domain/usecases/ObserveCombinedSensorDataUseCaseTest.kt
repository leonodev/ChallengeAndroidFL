package com.geobeacon.tracker.domain.usecases

import app.cash.turbine.test
import com.geobeacon.tracker.domain.models.BleDevice
import com.geobeacon.tracker.domain.models.GeoPoint
import com.geobeacon.tracker.domain.repositories.DeviceScanner
import com.geobeacon.tracker.domain.repositories.LocationTracker
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
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
        val locationTracker = FakeLocationTracker(
            flow { emit(GeoPoint(-2.0, -79.0)) }
        )
        val deviceScanner = FakeDeviceScanner(
            flow { emit(BleDevice("AA:BB:CC:DD:EE:FF", -70, 1000L)) }
        )

        val useCase = ObserveCombinedSensorDataUseCase(locationTracker, deviceScanner)

        useCase().test {
            val item = awaitItem()
            assertEquals("AA:BB:CC:DD:EE:FF", item.macAddress)
            assertEquals(-70, item.rssi)
            assertEquals(-2.0, item.location.latitude, 0.001)
            assertEquals(-79.0, item.location.longitude, 0.001)
            awaitComplete()
        }
    }

    @Test
    fun `test late GPS location emits correctly once available`() = runTest {
        val locationTracker = FakeLocationTracker(
            flow {
                delay(1000)
                emit(GeoPoint(-2.5, -79.5))
            }
        )
        val deviceScanner = FakeDeviceScanner(
            flow { emit(BleDevice("11:22:33:44:55:66", -65, 2000L)) }
        )

        val useCase = ObserveCombinedSensorDataUseCase(locationTracker, deviceScanner)

        useCase().test {
            val item = awaitItem()
            assertEquals("11:22:33:44:55:66", item.macAddress)
            assertEquals(-2.5, item.location.latitude, 0.001)
            assertEquals(-79.5, item.location.longitude, 0.001)
            awaitComplete()
        }
    }
}
