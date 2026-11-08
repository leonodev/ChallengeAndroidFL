package com.geobeacon.tracker.data.mapper

import com.geobeacon.tracker.data.local.SensorEntity
import com.geobeacon.tracker.domain.models.GeoPoint
import com.geobeacon.tracker.domain.models.SensorEvent

fun SensorEntity.toDomain(): SensorEvent {
    return SensorEvent(
        id = id.toLong(),
        macAddress = macAddress,
        rssi = rssi,
        location = GeoPoint(latitude = latitude, longitude = longitude, timestamp = timestamp),
        timestamp = timestamp
    )
}

fun SensorEvent.toEntity(synced: Boolean = false): SensorEntity {
    return SensorEntity(
        id = if (id > 0L) id.toInt() else 0,
        macAddress = macAddress,
        rssi = rssi,
        latitude = location.latitude,
        longitude = location.longitude,
        timestamp = timestamp,
        synced = synced
    )
}
