package com.geobeacon.tracker.domain.usecases

import com.geobeacon.tracker.domain.models.SensorEvent
import com.geobeacon.tracker.domain.repositories.SensorRepository
import javax.inject.Inject

class StartTrackingUseCase @Inject constructor(
    private val sensorRepository: SensorRepository
) {
    suspend operator fun invoke(event: SensorEvent) {
        sensorRepository.insert(event)
    }
}
