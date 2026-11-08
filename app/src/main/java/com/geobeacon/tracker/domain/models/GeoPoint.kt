package com.geobeacon.tracker.domain.models

data class GeoPoint(
    val latitude: Double,
    val longitude: Double,
    val timestamp: Long = System.currentTimeMillis()
)
