package com.geobeacon.tracker.data
import com.geobeacon.tracker.domain.BleDevice
import com.geobeacon.tracker.domain.DeviceScanner
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import kotlin.random.Random

class MockBleScanner @Inject constructor() : DeviceScanner {
    override fun scanForDevices(): Flow<BleDevice> = flow {
        while (true) {
            val mac = String.format("%02X:%02X:%02X:%02X:%02X:%02X",
                Random.nextInt(256), Random.nextInt(256), Random.nextInt(256),
                Random.nextInt(256), Random.nextInt(256), Random.nextInt(256)
            )
            emit(BleDevice(macAddress = mac, rssi = Random.nextInt(-100, -30), timestamp = System.currentTimeMillis()))
            delay(3000)
        }
    }
}
