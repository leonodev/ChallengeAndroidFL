package com.geobeacon.tracker.service

import com.geobeacon.tracker.di.ApplicationScope
import com.geobeacon.tracker.di.IoDispatcher
import com.geobeacon.tracker.domain.repositories.SensorRepository
import com.geobeacon.tracker.domain.repositories.SyncRepository
import kotlinx.coroutines.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SyncManager @Inject constructor(
    private val sensorRepository: SensorRepository,
    private val syncRepository: SyncRepository,
    @ApplicationScope private val externalScope: CoroutineScope,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {
    private var syncJob: Job? = null

    fun startSyncLoop(intervalMillis: Long = TimeUnit.MINUTES.toMillis(15)) {
        if (syncJob?.isActive == true) return
        syncJob = externalScope.launch(ioDispatcher) {
            while (isActive) {
                try {
                    val unsynced = sensorRepository.getUnsynchronizedEvents()
                    if (unsynced.isNotEmpty()) {
                        val success = syncRepository.syncEvents(unsynced)
                        if (success) {
                            val ids = unsynced.map { it.id }
                            sensorRepository.markAsSynchronized(ids)
                        }
                    }
                } catch (e: Exception) {
                    // Retry on next interval without data loss
                }
                delay(intervalMillis)
            }
        }
    }

    fun stopSyncLoop() {
        syncJob?.cancel()
        syncJob = null
    }
}
