package com.geobeacon.tracker

import android.content.Intent
import android.os.Bundle
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnToggle.setOnClickListener {
            val isCurrentlyTracking = viewModel.uiState.value.isTracking
            val newState = !isCurrentlyTracking
            viewModel.onAction(TrackingAction.ToggleTracking(newState))

            val serviceIntent = Intent(this, TrackingService::class.java)
            if (newState) {
                ContextCompat.startForegroundService(this, serviceIntent)
            } else {
                serviceIntent.action = "STOP_TRACKING"
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
}
