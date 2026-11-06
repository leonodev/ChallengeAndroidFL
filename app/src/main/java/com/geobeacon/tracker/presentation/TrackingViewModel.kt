package com.geobeacon.tracker.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.geobeacon.tracker.domain.models.SensorEvent
import com.geobeacon.tracker.domain.usecases.GetLastEventsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

data class TrackingUiState(
    val isTracking: Boolean = false,
    val lastEvents: List<SensorEvent> = emptyList(),
    val error: String? = null
)

sealed interface TrackingAction {
    data class ToggleTracking(val start: Boolean) : TrackingAction
}

@HiltViewModel
class TrackingViewModel @Inject constructor(
    private val getLastEventsUseCase: GetLastEventsUseCase
) : ViewModel() {

    private val _isTracking = MutableStateFlow(false)
    private val _error = MutableStateFlow<String?>(null)

    val uiState: StateFlow<TrackingUiState> = combine(
        _isTracking,
        getLastEventsUseCase(),
        _error
    ) { isTracking, events, error ->
        TrackingUiState(
            isTracking = isTracking,
            lastEvents = events,
            error = error
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = TrackingUiState()
    )

    fun onAction(action: TrackingAction) {
        when (action) {
            is TrackingAction.ToggleTracking -> {
                _isTracking.value = action.start
            }
        }
    }
}
