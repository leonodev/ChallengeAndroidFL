# Fase 1: Capa de Dominio (El Núcleo).


## Contexto y Objetivo
- El objetivo de esta fase es establecer el núcleo de la aplicación mediante la creación de los modelos de negocio, interfaces (contratos) y casos de uso, asegurando cero acoplamiento con capas que no deberia interactuar bajo una arquitectura clean (Onion).
- Al finalizar todas y cada una de las intrucciones, debes verificar y compilar el proyecto para garantizar su estabilidad tras finalizar esta fase

### 1. Instrucciones para la definición de Modelos y Contratos
> "Dentro de la capa de dominio previamente creada, necesitamos los modelos puros para representar el punto GPS, el dispositivo BLE y el evento combinado (SensorEvent). También define las interfaces para los repositorios que se encargarán del almacenamiento local de sensores y de la sincronización con el servidor remoto. Recuerda, ninguna de estas clases debe importar componentes del SDK de Android o anotaciones de base de datos."

**Resultado esperado:**
- **Modelos puros de Kotlin**:
  - `GeoPoint.kt`: Coordenadas geográficas (`latitude`, `longitude`) y timestamp.
  - `BleDevice.kt`: Dispositivo BLE detectado (`macAddress`, `rssi`, `timestamp`).
  - `SensorEvent.kt`: Evento combinado final que unifica BLE y GPS.
- **Contratos (Inversión de Dependencias)**:
  - `SensorRepository.kt`: Interfaz que define las operaciones de inserción, observación de eventos recientes mediante `Flow`, consulta de registros no sincronizados y marcado de éxito.
  - `SyncRepository.kt`: Interfaz pura para orquestar la sincronización de una lista de `SensorEvent`.
  - `DeviceScanner.kt` y `LocationTracker.kt`: Interfaces que representan los flujos de hardware abstraídos.
  - `TrackingController.kt`: Interfaz que expone un `StateFlow` del estado de seguimiento y acciones para iniciar/detener, desacoplando así la capa de servicio de los casos de uso.
 
### 2. Extracción de Lógica a Casos de Uso
**Instrucción proporcionada:** 
> "Ahora, extrae toda la lógica de negocio y de orquestación en Casos de Uso (Use Cases) independientes para aplicar el principio de Responsabilidad Única (SRP) y lograr máxima cohesibilidad y testabilidad de los requerimientos funcionales del challenge."

**Resultado esperado:**
- Múltiples UseCases independientes, inyectables mediante el constructor:
  - `GetLastEventsUseCase`: Encapsula la obtención reactiva de los últimos eventos registrados.
  - `StartTrackingUseCase` y `StopTrackingUseCase`: Aíslan la lógica de encendido y apagado del sistema de rastreo a través de la abstracción `TrackingController`.
  - `ObserveTrackingStateUseCase`: Expone el estado actual del servicio hacia la UI.
  - `RecordSensorEventsUseCase` e `InsertSensorEventUseCase`: Abstracciones para orquestar el flujo hacia el repositorio.