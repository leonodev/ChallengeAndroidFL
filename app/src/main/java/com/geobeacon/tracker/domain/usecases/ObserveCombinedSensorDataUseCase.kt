package com.geobeacon.tracker.domain.usecases

import com.geobeacon.tracker.di.IoDispatcher
import com.geobeacon.tracker.domain.models.SensorEvent
import com.geobeacon.tracker.domain.repositories.DeviceScanner
import com.geobeacon.tracker.domain.repositories.LocationTracker
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import javax.inject.Inject

class ObserveCombinedSensorDataUseCase @Inject constructor(
    private val locationTracker: LocationTracker,
    private val deviceScanner: DeviceScanner,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(): Flow<SensorEvent> {
        return deviceScanner.scanForDevices()
            .buffer() // Control data pressure and backpressure
            .conflate() // Conflate rapid emissions if needed
            .flatMapLatest { bleDevice ->
                locationTracker.getLocationFlow()
                    .take(1) // Get the latest known location at the moment of BLE scan
                    .map { location ->
                        SensorEvent(
                            macAddress = bleDevice.macAddress,
                            rssi = bleDevice.rssi,
                            latitude = location.latitude,
                            longitude = location.longitude,
                            timestamp = bleDevice.timestamp,
                            synced = false
                        )
                    }
            }
            .flowOn(ioDispatcher) // Injectable dispatcher for testability
    }
}
