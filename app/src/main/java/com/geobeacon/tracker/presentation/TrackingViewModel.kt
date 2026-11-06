package com.geobeacon.tracker.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.geobeacon.tracker.domain.usecases.GetLastEventsUseCase
import com.geobeacon.tracker.domain.usecases.ObserveTrackingStateUseCase
import com.geobeacon.tracker.domain.usecases.StartTrackingUseCase
import com.geobeacon.tracker.domain.usecases.StopTrackingUseCase
import com.geobeacon.tracker.presentation.model.SensorEventUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

data class TrackingUiState(
    val isTracking: Boolean = false,
    val lastEvents: List<SensorEventUiModel> = emptyList(),
    val error: String? = null
)

sealed interface TrackingAction {
    data class ToggleTracking(val start: Boolean) : TrackingAction
}

@HiltViewModel
class TrackingViewModel @Inject constructor(
    private val getLastEventsUseCase: GetLastEventsUseCase,
    private val observeTrackingStateUseCase: ObserveTrackingStateUseCase,
    private val startTrackingUseCase: StartTrackingUseCase,
    private val stopTrackingUseCase: StopTrackingUseCase
) : ViewModel() {

    private val dateFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())

    val uiState: StateFlow<TrackingUiState> = combine(
        observeTrackingStateUseCase(),
        getLastEventsUseCase()
    ) { isTracking, events ->
        val uiModels = events.map { event ->
            SensorEventUiModel(
                id = event.id,
                formattedTime = dateFormat.format(Date(event.timestamp)),
                macAddress = event.macAddress,
                rssiText = "RSSI: ${event.rssi} dBm",
                latLonText = String.format(Locale.US, "Lat: %.4f, Lon: %.4f", event.location.latitude, event.location.longitude)
            )
        }
        TrackingUiState(
            isTracking = isTracking,
            lastEvents = uiModels
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = TrackingUiState()
    )

    fun onAction(action: TrackingAction) {
        when (action) {
            is TrackingAction.ToggleTracking -> {
                if (action.start) {
                    startTrackingUseCase()
                } else {
                    stopTrackingUseCase()
                }
            }
        }
    }
}
