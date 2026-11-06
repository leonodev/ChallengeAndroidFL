package com.geobeacon.tracker.service

import android.content.Context
import androidx.work.*
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SyncManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    fun startSyncLoop() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val syncRequest = PeriodicWorkRequestBuilder<SyncWorker>(15, TimeUnit.MINUTES)
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "GeoBeaconSyncWork",
            ExistingPeriodicWorkPolicy.KEEP,
            syncRequest
        )
    }

    fun stopSyncLoop() {
        WorkManager.getInstance(context).cancelUniqueWork("GeoBeaconSyncWork")
    }
}
