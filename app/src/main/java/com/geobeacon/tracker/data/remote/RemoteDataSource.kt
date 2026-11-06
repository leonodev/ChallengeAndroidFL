package com.geobeacon.tracker.data.remote

import com.geobeacon.tracker.domain.models.SensorEvent
import kotlinx.coroutines.delay
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

interface RemoteDataSource {
    suspend fun uploadEvents(events: List<SensorEvent>): Boolean
}

@Singleton
class MockRemoteDataSource @Inject constructor() : RemoteDataSource {
    override suspend fun uploadEvents(events: List<SensorEvent>): Boolean {
        if (events.isEmpty()) return true
        delay(1500) // Simulate network latency
        if (Random.nextDouble() < 0.1) {
            throw RuntimeException("Network error during mock sync")
        }
        return true
    }
}
