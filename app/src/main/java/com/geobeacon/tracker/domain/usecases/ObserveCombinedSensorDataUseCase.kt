package com.geobeacon.tracker.domain.usecases

import com.geobeacon.tracker.domain.models.BleDevice
import com.geobeacon.tracker.domain.models.GeoPoint
import com.geobeacon.tracker.domain.models.SensorEvent
import com.geobeacon.tracker.domain.repositories.DeviceScanner
import com.geobeacon.tracker.domain.repositories.LocationTracker
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class ObserveCombinedSensorDataUseCase @Inject constructor(
    private val locationTracker: LocationTracker,
    private val deviceScanner: DeviceScanner
) {
    operator fun invoke(): Flow<SensorEvent> = channelFlow {
        var currentLocation: GeoPoint? = null
        val pendingDevices = mutableListOf<BleDevice>()

        // One GPS subscription, tied to the lifetime of this collection
        val locationJob = launch {
            locationTracker.getLocationFlow().collect { location ->
                currentLocation = location

                // If location arrives and we have pending devices, emit them all
                if (pendingDevices.isNotEmpty()) {
                    pendingDevices.forEach { device ->
                        send(
                            SensorEvent(
                                macAddress = device.macAddress,
                                rssi = device.rssi,
                                location = location,
                                timestamp = device.timestamp
                            )
                        )
                    }
                    pendingDevices.clear() // Clear the buffer
                }
            }
        }

        try {
            deviceScanner.scanForDevices().collect { device ->
                val location = currentLocation
                if (location == null) {
                    // No GPS yet: buffer the device to avoid backpressure/data loss
                    pendingDevices.add(device)
                } else {
                    // GPS available: emit immediately
                    send(
                        SensorEvent(
                            macAddress = device.macAddress,
                            rssi = device.rssi,
                            location = location,
                            timestamp = device.timestamp
                        )
                    )
                }
            }
        } finally {
            locationJob.cancel()
        }
    }
}
