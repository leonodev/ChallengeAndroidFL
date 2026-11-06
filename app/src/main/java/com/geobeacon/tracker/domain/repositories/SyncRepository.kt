package com.geobeacon.tracker.domain.repositories

import com.geobeacon.tracker.domain.models.SensorEvent

interface SyncRepository {
    suspend fun syncEvents(events: List<SensorEvent>): Boolean
}
