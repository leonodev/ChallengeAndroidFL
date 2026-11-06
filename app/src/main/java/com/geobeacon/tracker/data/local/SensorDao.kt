package com.geobeacon.tracker.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SensorDao {
    @Insert
    suspend fun insert(data: SensorEntity)

    @Query("SELECT * FROM sensor_data ORDER BY timestamp DESC LIMIT 10")
    fun getLatestEvents(): Flow<List<SensorEntity>>

    @Query("SELECT * FROM sensor_data WHERE synced = 0")
    suspend fun getUnsynchronizedEvents(): List<SensorEntity>

    @Query("UPDATE sensor_data SET synced = 1 WHERE id IN (:ids)")
    suspend fun markAsSynchronized(ids: List<Long>)
}
