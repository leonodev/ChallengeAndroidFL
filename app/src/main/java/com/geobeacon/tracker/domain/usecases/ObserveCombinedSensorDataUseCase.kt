package com.geobeacon.tracker.domain.usecases

import com.geobeacon.tracker.domain.models.GeoPoint
import com.geobeacon.tracker.domain.models.SensorEvent
import com.geobeacon.tracker.domain.repositories.DeviceScanner
import com.geobeacon.tracker.domain.repositories.LocationTracker
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

class ObserveCombinedSensorDataUseCase @Inject constructor(
    private val locationTracker: LocationTracker,
    private val deviceScanner: DeviceScanner
) {
    operator fun invoke(): Flow<SensorEvent> = channelFlow {
        val lastLocation = MutableStateFlow<GeoPoint?>(null)

        // One GPS subscription, tied to the lifetime of this collection
        val locationJob = launch {
            locationTracker.getLocationFlow().collect { lastLocation.value = it }
        }

        try {
            deviceScanner.scanForDevices().collect { device ->
                // Suspends until location is available; then emits event
                val location = lastLocation.filterNotNull().first()
                send(
                    SensorEvent(
                        macAddress = device.macAddress,
                        rssi = device.rssi,
                        location = location,
                        timestamp = device.timestamp
                    )
                )
            }
        } finally {
            locationJob.cancel()
        }
    }
}
