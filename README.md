# GeoBeacon Tracker

GeoBeacon Tracker es una aplicación Android desarrollada en **Kotlin** bajo los principios de **Clean Architecture** y **MVVM**, diseñada para la captura en segundo plano de ubicación GPS y escaneo de dispositivos BLE (Bluetooth Low Energy), persistencia local reactiva con Room y sincronización periódica simulada.

---

## 🚀 Cómo Compilar y Ejecutar

### Requisitos
- **Android Studio** (Hedgehog o superior recomendado).
- **JDK 17**.
- **Android SDK** (Compile SDK / Target SDK: 34, Min SDK: 26).

### Pasos
1. Clonar o abrir el proyecto en Android Studio.
2. Sincronizar el proyecto con Gradle (`File > Sync Project with Gradle Files`).
3. Ejecutar las pruebas unitarias:
   ```bash
   ./gradlew testDebugUnitTest
   ```
4. Compilar e instalar la app en un emulador o dispositivo físico (API 26+):
   ```bash
   ./gradlew assembleDebug
   ```

---

## 🏛️ Decisiones de Arquitectura

El proyecto está estructurado estrictamente en tres capas principales:

1. **Capa de Dominio (`domain/`)**:
   - Modelos puros de Kotlin (`models/`: `GeoPoint`, `BleDevice`, `SensorEvent`).
   - Contratos de repositorios (`repositories/`: `SensorRepository`, `SyncRepository`, `DeviceScanner`, `LocationTracker`).
   - Casos de uso (`usecases/`: `StartTrackingUseCase`, `StopTrackingUseCase`, `ObserveCombinedSensorDataUseCase`, `GetLastEventsUseCase`).
   - **Cero dependencias de Android o Room**.

2. **Capa de Datos (`data/`)**:
   - Base de datos Room (`AppDatabase`, `SensorDao`, `SensorEntity` con campo `synced`).
   - Mappers (`SensorMapper.kt`) para transformaciones bidireccionales Entity ↔ Domain.
   - Implementaciones de repositorios (`SensorRepositoryImpl`, `SyncRepositoryImpl`) y fuente de datos remota (`MockRemoteDataSource`).

3. **Capa de Presentación y Servicios (`presentation/` y `service/`)**:
   - `TrackingViewModel` con patrón MVI ligero y `StateFlow` respaldado por `SharingStarted.WhileSubscribed(5000)`.
   - `TrackingService` (Foreground Service) conectado al `TrackingManager`/`SyncManager` para persistencia en segundo plano.

---

## 🌊 Justificación sobre la Combinación de Flujos (Fase 2)

- **Operador Elegido**: **`flatMapLatest`** combinado con `.take(1)` sobre el flujo de ubicación.
- **Por qué no `combine` puro**: `combine` emite cada vez que *cualquiera* de los dos flujos emite. Si el GPS se actualiza frecuentemente, volvería a registrar el mismo dispositivo BLE una y otra vez aunque este no haya cambiado, inflando la base de datos innecesariamente.
- **La Solución**: Cada vez que el escáner BLE detecta un dispositivo (`deviceScanner.scanForDevices()`), tomamos la **última posición GPS conocida** en ese instante exacto (`locationTracker.getLocationFlow().take(1)`). Esto garantiza un registro preciso por cada escaneo BLE enriquecido con la geolocalización actual sin duplicados redundantes.
- **Control de Presión (Backpressure)**: Se aplicaron los operadores `.buffer()` y `.conflate()` para gestionar ráfagas de emisiones de dispositivos BLE.
- **Testabilidad**: Todos los dispatchers se inyectan mediante el calificador `@IoDispatcher`, permitiendo el uso de `TestDispatcher` en las pruebas unitarias.

---

## ⚖️ Trade-offs y Mejoras para Producción

1. **Sincronización**: Actualmente se ejecuta mediante un loop en una corrutina de aplicación gestionada por `SyncManager`. Para producción, se recomienda migrar a **`WorkManager`** con restricciones de red y batería para garantizar entregas garantizadas incluso ante reinicios del dispositivo.
2. **Reintentos**: Se implementó un reintento simple por intervalo; en producción se debe añadir **backgrowth / backoff exponencial**.
3. **Retención de Datos**: Se utiliza una política simple de últimos 10 eventos en UI, pero en producción se requiere una política de purgado en Room para evitar crecimiento desmedido de la base de datos.
4. **Observabilidad y CI**: Integración de Crashlytics, logging estructurado y pipelines de CI/CD para tests instrumentados y de DAO.
