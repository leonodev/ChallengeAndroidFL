# Fase 4: Presentación y Foreground Service

## Contexto y Objetivo
El objetivo de esta fase es conectar el núcleo de dominio (y su motor de datos) con la interfaz de usuario, garantizando que el estado de la aplicación sea reactivo y que el servicio de rastreo pueda mantenerse vivo en segundo plano respetando las políticas modernas de Android.
Al finalizar todas y cada una de las instrucciones, debes verificar y compilar el proyecto para garantizar su estabilidad tras finalizar esta fase.

## Instrucción proporcionada: 
> "Implementa la capa de presentación (presentation). Crea el `TrackingViewModel` que gestione el estado usando `StateFlow`. Utiliza el operador `combine` para unir la emisión del estado del servicio de tracking y la lectura en tiempo real de los últimos eventos de la base de datos (Room). Asegúrate de exponer eventos UI mapeados y de gestionar las acciones del usuario de manera centralizada ."

**Resultado esperado:**
- **ViewModel (`TrackingViewModel`)**:
   - Implementación de un flujo de datos unidireccional (Single Source of Truth) combinando `observeTrackingStateUseCase()` y `getLastEventsUseCase()`.
   - Uso de `SharingStarted.WhileSubscribed(5000)` para pausar la lectura de la base de datos cuando la app está en background, ahorrando batería.
   - Creación de estados de UI encapsulados (`TrackingUiState`) y modelos mapeados listos para mostrar (`SensorEventUiModel`).
   - Abstracción de intenciones del usuario mediante la clase sellada `TrackingAction`.

### 2. Foreground Service (TrackingService)
**Instrucción proporcionada:** 
> "Crea el `TrackingService` que herede de Service de Android. Este servicio debe ejecutarse en Foreground para mantener viva la aplicación. Su única responsabilidad debe ser encender el servicio, iniciar el rastreo usando el caso de uso `RecordSensorEventsUseCase` e iniciar el orquestador de WorkManager `SyncScheduler`. Mantén el servicio desacoplado de la lógica compleja."

**Resultado esperado:**
- **Servicio en Primer Plano (`TrackingService`)**:
   - Configuración de un *Foreground Service* con un canal de notificaciones y permisos explícitos (`FOREGROUND_SERVICE_TYPE_LOCATION`).
   - Orquestación de dependencias inyectadas por Hilt (`@AndroidEntryPoint`).
   - Gestión de su propio `CoroutineScope` acoplado al ciclo de vida del servicio.
   - Inicio cruzado del rastreo local (`recordSensorEventsUseCase()`) y del programador de sincronización en la nube (`syncScheduler.startSyncWork()`).
