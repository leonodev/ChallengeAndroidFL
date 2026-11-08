package com.geobeacon.tracker.domain.repositories

import com.geobeacon.tracker.domain.models.BleDevice
import kotlinx.coroutines.flow.Flow

interface DeviceScanner {
    fun scanForDevices(): Flow<BleDevice>
}
