package com.geobeacon.tracker.domain.usecases

import com.geobeacon.tracker.domain.models.SensorEvent
import com.geobeacon.tracker.domain.repositories.SensorRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetLastEventsUseCase @Inject constructor(
    private val sensorRepository: SensorRepository
) {
    operator fun invoke(): Flow<List<SensorEvent>> {
        return sensorRepository.observeLatestEvents()
    }
}
