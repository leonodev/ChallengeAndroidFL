package com.geobeacon.tracker.data
import com.geobeacon.tracker.domain.LocationData
import com.geobeacon.tracker.domain.LocationTracker
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class MockLocationTracker @Inject constructor() : LocationTracker {
    override fun getLocationFlow(): Flow<LocationData> = flow {
        var lat = -2.897350
        var lon = -79.004510
        while (true) {
            emit(LocationData(latitude = lat, longitude = lon))
            lat += 0.0001
            lon += 0.0001
            delay(5000)
        }
    }
}
