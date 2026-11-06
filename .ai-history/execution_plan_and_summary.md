# Plan de Ejecución y Resumen Arquitectónico (GeoBeacon Tracker)

Este documento registra el plan de desarrollo ejecutado fase por fase para el challenge técnico de GeoBeacon Tracker, reflejando el análisis previo, las decisiones técnicas adoptadas.

---

## Resumen de Fases y Decisiones Técnicas

### Fase 0: Reconocimiento y Preparación
- **Análisis del Boilerplate**: Inspección de `LocationTracker`, `DeviceScanner`, Room DAO y servicios base.
- **Estructura de Paquetes**: Adopción de Clean Architecture + MVVM con nombres en plural y estrictamente segregados:
  - `domain/models/`, `domain/repositories/`, `domain/usecases/`
  - `data/local/`, `data/remote/`, `data/repository/`, `data/mapper/`
  - `presentation/`, `di/`, `service/`
- **Gestión de Concurrencia**: Uso de despachadores inyectados (`@IoDispatcher`) y scopes de aplicación para operaciones en segundo plano no bloqueantes.

### Fase 1: Capa de Dominio
- **Modelos Puros**: `GeoPoint`, `BleDevice`, `SensorEvent` (sin dependencias de Android ni Room).
- **Contratos**: `SensorRepository`, `SyncRepository`, `DeviceScanner`, `LocationTracker`.
- **Casos de Uso**: `StartTrackingUseCase`, `StopTrackingUseCase`, `ObserveCombinedSensorDataUseCase`, `GetLastEventsUseCase`.

### Fase 2: Combinación de Flujos
- **Operador Elegido**: `flatMapLatest` combinado con `.take(1)` sobre el flujo de ubicación.
- **Justificación**: Garantiza un registro preciso por cada escaneo BLE enriquecido con la última posición GPS conocida, evitando duplicados redundantes generados por tics continuos del GPS.
- **Control de Presión**: Aplicación de `.buffer()` y `.conflate()` para manejar ráfagas de emisiones BLE.

### Fase 3: Capa de Datos
- **Room**: Adición del campo `synced: Boolean` en `SensorEntity`, consultas DAO para elementos no sincronizados y marcado masivo por IDs. Mapeadores bidireccionales (`SensorMapper`).
- **Implementación**: `SensorRepositoryImpl` con ejecución en hilos de IO mediante `withContext` y `flowOn`.

### Fase 4: Sincronización Periódica con WorkManager
- **WorkManager**: Implementación de `SyncWorker` anotado con `@HiltWorker` y `@AssistedInject`.
- **Configuración**: Tarea periódica única (`enqueueUniquePeriodicWork`) cada 15 minutos con restricciones explícitas de red (`NetworkType.CONNECTED`) e integración con `GeoBeaconApp` mediante `Configuration.Provider`.

### Fase 5: ViewModel y UI Mínima
- **TrackingViewModel**: Estado reactivo con `StateFlow` y `SharingStarted.WhileSubscribed(5000)`, gestionando acciones de usuario (MVI ligero).
- **TrackingService**: Foreground Service configurado con `ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION` y manejo de permisos en tiempo de ejecución para Android 14 (API 34+).
- **MainActivity**: Interfaz limpia con ViewBinding y observación de eventos en tiempo real.

### Fase 6: Pruebas Unitarias
- **Turbine & Coroutines Test**: Cobertura de tests unitarios para la combinación de flujos (`ObserveCombinedSensorDataUseCaseTest`) y el ViewModel (`TrackingViewModelTest`).

### Fase 7: README y Entrega
- Documentación exhaustiva en [README.md](file:///Users/fredyleon/Downloads/GeoBeacon/README.md) detallando arquitectura, decisiones sobre flujos, WorkManager y mejoras para producción.
