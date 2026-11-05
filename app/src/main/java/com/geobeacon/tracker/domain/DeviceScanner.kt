package com.geobeacon.tracker.domain
import kotlinx.coroutines.flow.Flow
data class BleDevice(val macAddress: String, val rssi: Int, val timestamp: Long)
interface DeviceScanner {
    fun scanForDevices(): Flow<BleDevice>
}
