# GeoBeacon Tracker

GeoBeacon Tracker es una aplicación Android desarrollada en **Kotlin** bajo los principios de **Clean Architecture estricta (Arquitectura Onion)**, diseñada para la captura en segundo plano de ubicación GPS y escaneo de dispositivos BLE (Bluetooth Low Energy), persistencia local reactiva con Room y sincronización periódica garantizada con **WorkManager**.

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

## 🏛️ Decisiones de Arquitectura (Clean Architecture / Onion)

El proyecto respeta estrictamente la **Regla de Dependencia** apuntando siempre hacia el centro (Dominio):

1. **Capa de Dominio (`domain/`)**:
   - Modelos puros de negocio (`models/`: `GeoPoint`, `BleDevice`, `SensorEvent` componiendo `GeoPoint`).
   - Contratos de repositorios (`repositories/`) y Casos de Uso (`usecases/`).
   - **Cero dependencias** de Android, Room o frameworks de Inyección de Dependencias (los Qualifiers residen en `core/`).

2. **Capa de Datos (`data/`)**:
   - Base de datos Room (`AppDatabase`, `SensorDao`, `SensorEntity` gestionando el estado de persistencia `synced`).
   - Mappers bidireccionales (`SensorMapper.kt`) para transformar Entity ↔ Domain.
   - Implementaciones de repositorios (`SensorRepositoryImpl`, `SyncRepositoryImpl`) y fuente de datos remota (`MockRemoteDataSource`).

3. **Capa de Presentación y Servicios (`presentation/` y `service/`)**:
   - `TrackingViewModel` con estado reactivo `StateFlow` (`SharingStarted.WhileSubscribed(5000)`).
   - `TrackingService` (Foreground Service) configurado con tipos de servicios en primer plano para ubicación (Android 14 API 34+) e integrado con WorkManager.

---

## 🌊 Justificación sobre la Combinación de Flujos (Fase 2)

- **Operador Elegido**: **`flatMapLatest`** combinado con un `StateFlow` en caliente (`stateIn`) y `.filterNotNull().take(1)`.
- **Por qué no `combine` puro**: `combine` emite cada vez que *cualquiera* de los dos flujos emite, lo que registraría duplicados redundantes de dispositivos BLE ante cada tick del GPS.
- **La Solución**: La ubicación se comparte como `StateFlow` (`SharingStarted.Eagerly`). Cuando el escáner BLE detecta un dispositivo, se combina con la última posición GPS conocida (`locationStateFlow.filterNotNull().take(1)`), garantizando un registro preciso por cada escaneo BLE sin duplicados y manejando de forma robusta la llegada tardía del GPS (*late GPS*).
- **Control de Presión (Backpressure)**: Se aplicó `.buffer(capacity = 64, onBufferOverflow = BufferOverflow.DROP_OLDEST)`.
- **Testabilidad**: Los despachadores se inyectan mediante el calificador `@IoDispatcher`, permitiendo pruebas unitarias deterministas con `TestDispatcher`.

---

## 🔄 Sincronización con WorkManager (Fase 4)

- **Por qué WorkManager**: Para garantizar que la sincronización de eventos pendientes con el servidor no dependa exclusivamente del Foreground Service.
- **Implementación**: `SyncWorker` (anotado con `@HiltWorker` y `@AssistedInject`) procesa registros pendientes de Room, los sube mediante `SyncRepository` y marca los IDs como sincronizados.
- **Configuración**: Tarea periódica única (`enqueueUniquePeriodicWork`) cada 15 minutos con restricciones de red (`NetworkType.CONNECTED`) y reintentos automáticos (`Result.retry()`).

---

## ⚖️ Trade-offs y Mejoras para Producción

1. **Backoff Exponencial**: Configurar `setBackoffCriteria` con retroceso exponencial en `SyncWorker`.
2. **Retención de Datos**: Política de purgado en Room para evitar crecimiento ilimitado de la base de datos a largo plazo.
3. **Observabilidad y CI**: Integración de Crashlytics, logging estructurado y pipelines de CI/CD para tests instrumentados y de DAO.
