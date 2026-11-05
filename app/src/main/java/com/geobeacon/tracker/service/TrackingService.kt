package com.geobeacon.tracker.service
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TrackingService : Service() {
    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        startForeground(1, createNotification())
    }
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // TODO: Combine LocationTracker and DeviceScanner flows here or in injected dependencies
        return START_STICKY
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
