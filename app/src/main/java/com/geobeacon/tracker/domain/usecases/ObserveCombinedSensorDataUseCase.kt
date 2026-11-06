package com.geobeacon.tracker.domain.usecases

import com.geobeacon.tracker.domain.repositories.DeviceScanner
import com.geobeacon.tracker.domain.repositories.LocationTracker
import javax.inject.Inject

class ObserveCombinedSensorDataUseCase @Inject constructor(
    private val locationTracker: LocationTracker,
    private val deviceScanner: DeviceScanner
) {
    fun getLocationFlow() = locationTracker.getLocationFlow()
    fun getDeviceScanFlow() = deviceScanner.scanForDevices()
}
