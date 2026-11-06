package com.geobeacon.tracker.service

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.geobeacon.tracker.domain.repositories.SensorRepository
import com.geobeacon.tracker.domain.repositories.SyncRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CancellationException

@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val sensorRepository: SensorRepository,
    private val syncRepository: SyncRepository
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val unsynced = sensorRepository.getUnsynchronizedEvents()
            if (unsynced.isNotEmpty()) {
                val success = syncRepository.syncEvents(unsynced)
                if (success) {
                    val eventIds = unsynced.map { it.id }
                    sensorRepository.markAsSynchronized(eventIds)
                } else {
                    return Result.retry()
                }
            }
            Result.success()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.retry()
        }
    }
}
