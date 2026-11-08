package com.geobeacon.tracker.domain.repositories
import kotlinx.coroutines.flow.StateFlow

interface TrackingController {
    val isTracking: StateFlow<Boolean>
    fun startTracking()
    fun stopTracking()
}
