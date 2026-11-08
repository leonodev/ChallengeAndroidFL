package com.geobeacon.tracker.domain.repositories

import com.geobeacon.tracker.domain.models.GeoPoint
import kotlinx.coroutines.flow.Flow

interface LocationTracker {
    fun getLocationFlow(): Flow<GeoPoint>
}
