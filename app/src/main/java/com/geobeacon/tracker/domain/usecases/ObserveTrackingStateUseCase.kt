package com.geobeacon.tracker.domain.usecases

import com.geobeacon.tracker.domain.repositories.TrackingController
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class ObserveTrackingStateUseCase @Inject constructor(
    private val trackingController: TrackingController
) {
    operator fun invoke(): StateFlow<Boolean> {
        return trackingController.isTracking
    }
}
