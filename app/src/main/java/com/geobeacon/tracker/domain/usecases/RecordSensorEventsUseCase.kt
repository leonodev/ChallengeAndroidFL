package com.geobeacon.tracker.domain.usecases

import com.geobeacon.tracker.domain.repositories.SensorRepository
import javax.inject.Inject

class RecordSensorEventsUseCase @Inject constructor(
    private val observeCombinedSensorDataUseCase: ObserveCombinedSensorDataUseCase,
    private val sensorRepository: SensorRepository
) {
    suspend operator fun invoke() {
        observeCombinedSensorDataUseCase().collect { event ->
            sensorRepository.insert(event)
        }
    }
}
