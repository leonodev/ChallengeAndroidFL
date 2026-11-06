# Phase 1: Capa de Dominio

## Implementación
- **Modelos puros de Kotlin**:
  - `GeoPoint.kt`: Coordenadas geográficas y timestamp.
  - `BleDevice.kt`: Dispositivo BLE detectado (macAddress, rssi, timestamp).
  - `SensorEvent.kt`: Evento combinado (id, macAddress, rssi, latitude, longitude, timestamp, synced).
- **Contratos / Interfaces de Repositorio**:
  - `SensorRepository.kt`: Inserción, observación de eventos recientes (`Flow`), consulta de no sincronizados y marcado como sincronizados.
  - `SyncRepository.kt`: Contrato para sincronización remota (`syncEvents`).
- **Use Cases**:
  - `GetLastEventsUseCase`: Obtener el flujo de últimos eventos.
  - `ObserveCombinedSensorDataUseCase`: Acceso a los flujos base de ubicación y escaneo BLE.
  - `StartTrackingUseCase`: Inserción de eventos de tracking.
  - `StopTrackingUseCase`: Lógica de parada de tracking.

## Verificación
- Compilación exitosa (`app:assembleDebug`) sin dependencias de Android ni Room en la capa de dominio.
