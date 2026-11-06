package com.geobeacon.tracker.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.geobeacon.tracker.core.IoDispatcher
import com.geobeacon.tracker.domain.usecases.RecordSensorEventsUseCase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class TrackingService : Service() {

    @Inject
    lateinit var recordSensorEventsUseCase: RecordSensorEventsUseCase

    @Inject
    lateinit var syncScheduler: SyncScheduler

    @Inject
    lateinit var trackingManager: TrackingManager

    @IoDispatcher
    @Inject
    lateinit var ioDispatcher: CoroutineDispatcher

    private val serviceScope by lazy { CoroutineScope(SupervisorJob() + ioDispatcher) }
    private var trackingJob: Job? = null

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
        if (action == ACTION_STOP) {
            stopTracking()
            stopSelf()
            return START_NOT_STICKY
        }

        startTracking()
        return START_STICKY
    }

    private fun startTracking() {
        if (trackingJob?.isActive == true) return
        trackingManager.setTracking(true)
        syncScheduler.startSyncWork()
        trackingJob = serviceScope.launch {
            recordSensorEventsUseCase()
        }
    }

    private fun stopTracking() {
        trackingJob?.cancel()
        trackingJob = null
        syncScheduler.stopSyncWork()
        trackingManager.setTracking(false)
    }

    override fun onDestroy() {
        stopTracking()
        serviceScope.cancel()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Location Tracking",
                NotificationManager.IMPORTANCE_LOW
            )
            getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        }
    }

    private fun createNotification(): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("GeoBeacon Active")
            .setContentText("Scanning for BLE devices and Location...")
            .setSmallIcon(android.R.drawable.ic_dialog_map)
            .build()
    }

    companion object {
        const val ACTION_STOP = "com.geobeacon.tracker.action.STOP_TRACKING"
        private const val CHANNEL_ID = "TRACKING_CHANNEL"

        fun startIntent(context: Context) = Intent(context, TrackingService::class.java)
        fun stopIntent(context: Context) = startIntent(context).setAction(ACTION_STOP)
    }
}
