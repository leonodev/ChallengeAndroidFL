package com.geobeacon.tracker.domain.repositories

import com.geobeacon.tracker.domain.models.SensorEvent
import kotlinx.coroutines.flow.Flow

interface SensorRepository {
    suspend fun insert(event: SensorEvent)
    fun observeLatestEvents(): Flow<List<SensorEvent>>
    suspend fun getUnsynchronizedEvents(): List<SensorEvent>
    suspend fun markAsSynchronized(ids: List<Long>)
}
