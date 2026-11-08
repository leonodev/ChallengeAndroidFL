package com.geobeacon.tracker.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [SensorEntity::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun sensorDao(): SensorDao
}
