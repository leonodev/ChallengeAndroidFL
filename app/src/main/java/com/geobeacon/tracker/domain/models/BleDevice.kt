package com.geobeacon.tracker.domain.models

data class BleDevice(
    val macAddress: String,
    val rssi: Int,
    val timestamp: Long
)
