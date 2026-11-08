package com.geobeacon.tracker.service

import android.content.Context
import com.geobeacon.tracker.domain.repositories.TrackingController
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TrackingManager @Inject constructor(
    @ApplicationContext private val context: Context
) : TrackingController {

    private val _isTracking = MutableStateFlow(false)
    override val isTracking: StateFlow<Boolean> = _isTracking.asStateFlow()

    override fun startTracking() {
        context.startService(TrackingService.startIntent(context))
    }

    override fun stopTracking() {
        context.startService(TrackingService.stopIntent(context))
    }

    internal fun setTracking(active: Boolean) {
        _isTracking.value = active
    }
}
