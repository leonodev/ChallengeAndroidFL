package com.geobeacon.tracker.domain.usecases

import com.geobeacon.tracker.core.ApplicationScope
import com.geobeacon.tracker.domain.models.GeoPoint
import com.geobeacon.tracker.domain.models.SensorEvent
import com.geobeacon.tracker.domain.repositories.DeviceScanner
import com.geobeacon.tracker.domain.repositories.LocationTracker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.*
import javax.inject.Inject

class ObserveCombinedSensorDataUseCase @Inject constructor(
    private val locationTracker: LocationTracker,
    private val deviceScanner: DeviceScanner,
    @ApplicationScope private val externalScope: CoroutineScope
) {
    private val locationStateFlow: StateFlow<GeoPoint?> = locationTracker.getLocationFlow()
        .stateIn(
            scope = externalScope,
            started = SharingStarted.Lazily,
            initialValue = null
        )

    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(): Flow<SensorEvent> {
        return deviceScanner.scanForDevices()
            .buffer(capacity = 64, onBufferOverflow = BufferOverflow.DROP_OLDEST)
            .flatMapLatest { bleDevice ->
                locationStateFlow
                    .filterNotNull()
                    .take(1)
                    .map { location ->
                        SensorEvent(
                            macAddress = bleDevice.macAddress,
                            rssi = bleDevice.rssi,
                            latitude = location.latitude,
                            longitude = location.longitude,
                            timestamp = bleDevice.timestamp
                        )
                    }
            }
    }
}
