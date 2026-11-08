package com.geobeacon.tracker.data.repository

import com.geobeacon.tracker.data.local.SensorDao
import com.geobeacon.tracker.data.mapper.toDomain
import com.geobeacon.tracker.data.mapper.toEntity
import com.geobeacon.tracker.domain.models.SensorEvent
import com.geobeacon.tracker.domain.repositories.SensorRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SensorRepositoryImpl @Inject constructor(
    private val sensorDao: SensorDao
) : SensorRepository {

    override suspend fun insert(event: SensorEvent) {
        sensorDao.insert(event.toEntity())
    }

    override fun observeLatestEvents(): Flow<List<SensorEvent>> {
        return sensorDao.getLatestEvents()
            .map { list -> list.map { it.toDomain() } }
    }

    override suspend fun getUnsynchronizedEvents(): List<SensorEvent> {
        return sensorDao.getUnsynchronizedEvents().map { it.toDomain() }
    }

    override suspend fun markAsSynchronized(ids: List<Long>) {
        sensorDao.markAsSynchronized(ids)
    }
}
