package com.geobeacon.tracker.presentation.model

data class SensorEventUiModel(
    val id: Long,
    val formattedTime: String,
    val macAddress: String,
    val rssiText: String,
    val latLonText: String
)
