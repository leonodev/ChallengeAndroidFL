package com.geobeacon.tracker.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.geobeacon.tracker.domain.usecases.ObserveCombinedSensorDataUseCase
import com.geobeacon.tracker.domain.usecases.StartTrackingUseCase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class TrackingService : Service() {

    @Inject
    lateinit var observeCombinedSensorDataUseCase: ObserveCombinedSensorDataUseCase

    @Inject
    lateinit var startTrackingUseCase: StartTrackingUseCase

    @Inject
    lateinit var syncManager: SyncManager

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        val notification = createNotification()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(1, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION)
        } else {
            startForeground(1, notification)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action = intent?.action
        if (action == "STOP_TRACKING") {
            stopTracking()
            stopSelf()
            return START_NOT_STICKY
        }

        startTracking()
        return START_STICKY
    }

    private fun startTracking() {
        syncManager.startSyncLoop()
        serviceScope.launch {
            observeCombinedSensorDataUseCase().collectLatest { event ->
                startTrackingUseCase(event)
            }
        }
    }

    private fun stopTracking() {
        syncManager.stopSyncLoop()
        serviceScope.cancel()
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "TRACKING_CHANNEL",
                "Location Tracking",
                NotificationManager.IMPORTANCE_LOW
            )
            getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        }
    }

    private fun createNotification(): Notification {
        return NotificationCompat.Builder(this, "TRACKING_CHANNEL")
            .setContentTitle("GeoBeacon Active")
            .setContentText("Scanning for BLE devices and Location...")
            .setSmallIcon(android.R.drawable.ic_dialog_map)
            .build()
    }
}
