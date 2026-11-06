package com.geobeacon.tracker.data.repository

import com.geobeacon.tracker.core.IoDispatcher
import com.geobeacon.tracker.data.local.SensorDao
import com.geobeacon.tracker.data.mapper.toDomain
import com.geobeacon.tracker.data.mapper.toEntity
import com.geobeacon.tracker.domain.models.SensorEvent
import com.geobeacon.tracker.domain.repositories.SensorRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SensorRepositoryImpl @Inject constructor(
    private val sensorDao: SensorDao,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : SensorRepository {

    override suspend fun insert(event: SensorEvent) {
        withContext(ioDispatcher) {
            sensorDao.insert(event.toEntity())
        }
    }

    override fun observeLatestEvents(): Flow<List<SensorEvent>> {
        return sensorDao.getLatestEvents()
            .map { list -> list.map { it.toDomain() } }
            .flowOn(ioDispatcher)
    }

    override suspend fun getUnsynchronizedEvents(): List<SensorEvent> {
        return withContext(ioDispatcher) {
            sensorDao.getUnsynchronizedEvents().map { it.toDomain() }
        }
    }

    override suspend fun markAsSynchronized(ids: List<Long>) {
        withContext(ioDispatcher) {
            sensorDao.markAsSynchronized(ids)
        }
    }
}
