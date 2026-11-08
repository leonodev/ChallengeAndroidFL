package com.geobeacon.tracker.di

import android.content.Context
import androidx.room.Room
import com.geobeacon.tracker.data.MockBleScanner
import com.geobeacon.tracker.data.MockLocationTracker
import com.geobeacon.tracker.data.local.AppDatabase
import com.geobeacon.tracker.data.remote.MockRemoteDataSource
import com.geobeacon.tracker.data.remote.RemoteDataSource
import com.geobeacon.tracker.data.repository.SensorRepositoryImpl
import com.geobeacon.tracker.data.repository.SyncRepositoryImpl
import com.geobeacon.tracker.domain.repositories.DeviceScanner
import com.geobeacon.tracker.domain.repositories.LocationTracker
import com.geobeacon.tracker.domain.repositories.SensorRepository
import com.geobeacon.tracker.domain.repositories.SyncRepository
import com.geobeacon.tracker.domain.repositories.TrackingController
import com.geobeacon.tracker.service.TrackingManager
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DomainModule {
    @Binds
    abstract fun bindDeviceScanner(mockBleScanner: MockBleScanner): DeviceScanner
    @Binds
    abstract fun bindLocationTracker(mockLocationTracker: MockLocationTracker): LocationTracker
    @Binds
    abstract fun bindSensorRepository(sensorRepositoryImpl: SensorRepositoryImpl): SensorRepository
    @Binds
    abstract fun bindRemoteDataSource(mockRemoteDataSource: MockRemoteDataSource): RemoteDataSource
    @Binds
    abstract fun bindSyncRepository(syncRepositoryImpl: SyncRepositoryImpl): SyncRepository
    @Binds
    abstract fun bindTrackingController(trackingManager: TrackingManager): TrackingController
}

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "sensor_database"
        )
            .fallbackToDestructiveMigration()
            .build()
    }
    @Provides
    fun provideSensorDao(database: AppDatabase) = database.sensorDao()
}
