package com.geobeacon.tracker.data.local
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sensor_data")
data class SensorEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val macAddress: String,
    val rssi: Int,
    val latitude: Double,
    val longitude: Double,
    val timestamp: Long
)
