package com.geobeacon.tracker.domain.usecases

import app.cash.turbine.test
import com.geobeacon.tracker.domain.models.BleDevice
import com.geobeacon.tracker.domain.models.GeoPoint
import com.geobeacon.tracker.domain.repositories.DeviceScanner
import com.geobeacon.tracker.domain.repositories.LocationTracker
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
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

    private fun device(mac: String) = BleDevice(macAddress = mac, rssi = -70, timestamp = System.currentTimeMillis())

    @Test
    fun `test combine flow emits correct sensor events`() = runTest {
        val locationTracker = FakeLocationTracker(
            flow {
                emit(GeoPoint(-2.0, -79.0))
                awaitCancellation()
            }
        )
        val deviceScanner = FakeDeviceScanner(
            flow {
                emit(device("AA:BB:CC:DD:EE:FF"))
                awaitCancellation()
            }
        )

        val useCase = ObserveCombinedSensorDataUseCase(locationTracker, deviceScanner)

        useCase().test {
            val item = awaitItem()
            assertEquals("AA:BB:CC:DD:EE:FF", item.macAddress)
            assertEquals(-70, item.rssi)
            assertEquals(-2.0, item.location.latitude, 0.001)
            assertEquals(-79.0, item.location.longitude, 0.001)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `emits one event per device even when the first fix arrives late`() = runTest {
        val location = flow {
            delay(100)
            emit(GeoPoint(-2.0, -79.0))
            awaitCancellation()
        }
        val devices = flow {
            emit(device("A"))
            emit(device("B"))
            emit(device("C"))
            awaitCancellation()
        }
        val useCase = ObserveCombinedSensorDataUseCase(
            FakeLocationTracker(location), FakeDeviceScanner(devices)
        )

        useCase().test {
            assertEquals(listOf("A", "B", "C"), List(3) { awaitItem().macAddress })
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `emits new coordinates when GPS changes between scans`() = runTest {
        val locationTracker = FakeLocationTracker(
            flow {
                emit(GeoPoint(-1.0, -78.0))
                delay(100)
                emit(GeoPoint(-2.0, -79.0))
                awaitCancellation()
            }
        )
        val deviceScanner = FakeDeviceScanner(
            flow {
                emit(device("A"))
                delay(150)
                emit(device("B"))
                awaitCancellation()
            }
        )

        val useCase = ObserveCombinedSensorDataUseCase(locationTracker, deviceScanner)

        useCase().test {
            val item1 = awaitItem()
            assertEquals("A", item1.macAddress)
            assertEquals(-1.0, item1.location.latitude, 0.001)

            val item2 = awaitItem()
            assertEquals("B", item2.macAddress)
            assertEquals(-2.0, item2.location.latitude, 0.001)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
