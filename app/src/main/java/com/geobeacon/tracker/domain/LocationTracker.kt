package com.geobeacon.tracker.domain
import kotlinx.coroutines.flow.Flow
data class LocationData(val latitude: Double, val longitude: Double)
interface LocationTracker {
    fun getLocationFlow(): Flow<LocationData>
}
