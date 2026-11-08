package com.geobeacon.tracker

import android.Manifest
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
import androidx.recyclerview.widget.LinearLayoutManager
import com.geobeacon.tracker.databinding.ActivityMainBinding
import com.geobeacon.tracker.presentation.TrackingAction
import com.geobeacon.tracker.presentation.TrackingViewModel
import com.geobeacon.tracker.presentation.adapter.SensorEventAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: TrackingViewModel by viewModels()
    private lateinit var adapter: SensorEventAdapter

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineLocationGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
        val coarseLocationGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false

        if (fineLocationGranted || coarseLocationGranted) {
            viewModel.onAction(TrackingAction.ToggleTracking(true))
        } else {
            Toast.makeText(this, "Location permission is required for tracking", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = SensorEventAdapter()
        binding.recyclerViewEvents.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewEvents.adapter = adapter

        binding.btnToggle.setOnClickListener {
            val isCurrentlyTracking = viewModel.uiState.value.isTracking
            val newState = !isCurrentlyTracking

            if (newState) {
                checkAndRequestPermissions()
            } else {
                viewModel.onAction(TrackingAction.ToggleTracking(false))
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

                    adapter.submitList(state.lastEvents)
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
            viewModel.onAction(TrackingAction.ToggleTracking(true))
        }
    }
}
