package com.geobeacon.tracker.domain.models

data class SensorEvent(
    val id: Long = 0L,
    val macAddress: String,
    val rssi: Int,
    val latitude: Double,
    val longitude: Double,
    val timestamp: Long,
    val synced: Boolean = false
)
