package com.geobeacon.tracker

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.geobeacon.tracker.databinding.ActivityMainBinding
import com.geobeacon.tracker.presentation.TrackingAction
import com.geobeacon.tracker.presentation.TrackingViewModel
import com.geobeacon.tracker.service.TrackingService
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: TrackingViewModel by viewModels()

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineLocationGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
        val coarseLocationGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false

        if (fineLocationGranted || coarseLocationGranted) {
            startTrackingService()
        } else {
            Toast.makeText(this, "Location permission is required for tracking", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnToggle.setOnClickListener {
            val isCurrentlyTracking = viewModel.uiState.value.isTracking
            val newState = !isCurrentlyTracking

            if (newState) {
                checkAndRequestPermissions()
            } else {
                viewModel.onAction(TrackingAction.ToggleTracking(false))
                val serviceIntent = Intent(this, TrackingService::class.java).apply {
                    action = "STOP_TRACKING"
                }
                startService(serviceIntent)
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collectLatest { state ->
                    if (state.isTracking) {
                        binding.btnToggle.text = "Stop Tracking"
                    } else {
                        binding.btnToggle.text = "Start Tracking"
                    }

                    val sb = StringBuilder()
                    val sdf = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
                    for (event in state.lastEvents) {
                        val timeStr = sdf.format(Date(event.timestamp))
                        sb.append("[$timeStr] MAC: ${event.macAddress}\n")
                        sb.append("  RSSI: ${event.rssi} | Lat: ${String.format(Locale.US, "%.4f", event.latitude)}, Lon: ${String.format(Locale.US, "%.4f", event.longitude)}\n")
                        sb.append("  Synced: ${event.synced}\n\n")
                    }
                    binding.tvEvents.text = if (sb.isEmpty()) "No tracking events yet." else sb.toString()
                }
            }
        }
    }

    private fun checkAndRequestPermissions() {
        val permissionsToRequest = mutableListOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionsToRequest.add(Manifest.permission.POST_NOTIFICATIONS)
        }

        val notGranted = permissionsToRequest.any {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }

        if (notGranted) {
            permissionLauncher.launch(permissionsToRequest.toTypedArray())
        } else {
            startTrackingService()
        }
    }

    private fun startTrackingService() {
        viewModel.onAction(TrackingAction.ToggleTracking(true))
        val serviceIntent = Intent(this, TrackingService::class.java)
        ContextCompat.startForegroundService(this, serviceIntent)
    }
}
