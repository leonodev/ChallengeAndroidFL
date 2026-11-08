package com.geobeacon.tracker.domain.usecases

import com.geobeacon.tracker.domain.repositories.TrackingController
import javax.inject.Inject

class StopTrackingUseCase @Inject constructor(
    private val trackingController: TrackingController
) {
    operator fun invoke() {
        trackingController.stopTracking()
    }
}
