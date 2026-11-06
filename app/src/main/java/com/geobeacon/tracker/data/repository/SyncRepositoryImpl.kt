package com.geobeacon.tracker.data.repository

import com.geobeacon.tracker.core.IoDispatcher
import com.geobeacon.tracker.data.remote.RemoteDataSource
import com.geobeacon.tracker.domain.models.SensorEvent
import com.geobeacon.tracker.domain.repositories.SyncRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SyncRepositoryImpl @Inject constructor(
    private val remoteDataSource: RemoteDataSource,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : SyncRepository {
    override suspend fun syncEvents(events: List<SensorEvent>): Boolean {
        return withContext(ioDispatcher) {
            try {
                remoteDataSource.uploadEvents(events)
            } catch (e: Exception) {
                false
            }
        }
    }
}
