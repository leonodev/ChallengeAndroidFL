package com.geobeacon.tracker.domain.usecases

import com.geobeacon.tracker.domain.repositories.TrackingController
import javax.inject.Inject

class StartTrackingUseCase @Inject constructor(
    private val trackingController: TrackingController
) {
    operator fun invoke() {
        trackingController.startTracking()
    }
}
