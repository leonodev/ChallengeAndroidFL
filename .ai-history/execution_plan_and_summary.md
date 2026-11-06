# Plan de Ejecución y Resumen Arquitectónico (GeoBeacon Tracker)

Este documento registra el plan de desarrollo ejecutado fase por fase para el challenge técnico de GeoBeacon Tracker, reflejando el análisis previo, las decisiones técnicas adoptadas y la evolución del código hacia un estándar de alta calidad.

---

## Resumen de Fases y Decisiones Técnicas

### Fase 0: Reconocimiento y Preparación (Arquitectura Base)
- **Análisis del Boilerplate**: Inspección de `LocationTracker`, `DeviceScanner`, Room DAO y servicios base.
- **Estructura de Paquetes**: Adopción estricta de Clean Architecture + Patrón MVVM (MVI-like) segregados por capas:
  - `domain/models/`, `domain/repositories/`, `domain/usecases/`
  - `data/local/`, `data/remote/`, `data/repository/`, `data/mapper/`
  - `presentation/`, `di/`, `core/`, `service/`
- **Gestión de Concurrencia**: Uso de inyección de dispatchers (`@IoDispatcher`) para facilitar el testing y aislar hilos.

### Fase 1: Capa de Dominio (El Núcleo)
- **Modelos Puros**: `GeoPoint`, `BleDevice`, `SensorEvent` (sin dependencias del SDK de Android, Room, o Retrofit).
- **Contratos (Inversión de Dependencias)**: Interfaces puras como `SensorRepository` y `SyncRepository`.
- **Casos de Uso**: Extracción de toda la lógica de negocio a UseCases independientes (`StartTrackingUseCase`, `StopTrackingUseCase`, `ObserveTrackingStateUseCase`, `GetLastEventsUseCase`, etc.) para lograr máxima cohesibilidad y testabilidad.

### Fase 2: Combinación de Flujos y Resiliencia (Resolviendo el *GPS Cold Start*)
- **Estrategia Elegida**: Uso de `channelFlow` con un buffer en memoria (`mutableListOf<BleDevice>`) y recolección paralela en lugar de operadores estándar.
- **Justificación Técnica**: Los operadores estándar como `combine`, `zip` o `flatMapLatest` son insuficientes para lidiar con las diferencias de hardware (el GPS puede tardar varios segundos en fijar, mientras que el BLE dispara inmediatamente). Congelar el flujo BLE esperando al GPS generaría *backpressure* y pérdida de tramas. Al usar un buffer local, capturamos todo el entorno BLE desde el milisegundo cero, y lo flusheamos hacia la base de datos en cuanto llega el primer *fix* de GPS. Cero pérdida de datos.

### Fase 3: Capa de Datos (Persistencia y Sincronización)
- **Room**: Uso de la base de datos local para guardar eventos. Implementación de Mappers (`SensorMapper`) bidireccionales para aislar las entidades de Room del resto de la app.
- **Sincronización (Network Sync)**: Implementación de `SyncWorker` (WorkManager) orquestado a través de `SyncScheduler`.
- **Justificación de WorkManager**: Aunque un `delay(15.minutes)` en un Foreground Service serviría, usar WorkManager garantiza las políticas de reintentos y restricciones de red (NetworkType.CONNECTED), mostrando alineación con los estándares modernos de Android.

### Fase 4: Presentación y Foreground Service
- **TrackingViewModel**: Gestión de estado reactivo y unidireccional con `StateFlow` y `combine`, reaccionando a los cambios en la base de datos local (Single Source of Truth) y al estado del Foreground Service.
- **TrackingService**: Desacoplado de la UI; orquesta los UseCases y maneja la notificación persistente para mantener la aplicación viva en segundo plano.

### Fase 5: Refactor y Pruebas Unitarias (Asegurando la Calidad)
- **Refactorización Continua**: Iteración sobre la arquitectura para extraer lógica del servicio hacia los Casos de Uso, garantizando que el servicio sea un mero orquestador.
- **Testing Asíncrono Avanzado**: Uso extensivo de la librería `Turbine` para probar la emisión correcta de eventos, las cancelaciones de flujos y la correcta inyección de `TestDispatchers` sin condiciones de carrera. Cobertura del escenario de *GPS Cold Start*.

### Fase 6: Documentación y Entrega
- Documentación exhaustiva y honesta en `README.md`, detallando arquitectura, la resolución del problema de asincronía de hardware, trade-offs (Doze Mode vs Foreground Service timing), y deuda técnica asumida para cumplir el tiempo estimado.