# Fase 0 y 1: Reconocimiento del Boilerplate y Definición del Dominio

## Contexto y Objetivo
En esta fase inicial, el objetivo fue analizar el código base proporcionado en el challenge (Room DAO base, `LocationTracker` y `DeviceScanner` simulados) y sentar las bases arquitectónicas del proyecto antes de escribir la lógica de implementación.

## Prompts / Decisiones Guiadas por IA

### 1. Definición de la Arquitectura Base (Fase 0)
**Instrucción proporcionada:** 
> "Analiza el boilerplate del proyecto. Vamos a utilizar Clean Architecture junto con MVVM. Necesitamos definir una estructura de paquetes estricta donde la capa no se evidencie el acoplamiento entre las distitas capas, es decir que este implementada como una arquitectura ONION donde las capas apunten hacia el centro. debes crear un modulo afnostico llamado core, donde alojaremos dispatcher para evitar posibles acoplamientos."

**Resultado y Decisiones Adoptadas:**
- Se estructuró el proyecto en paquetes claramente delimitados: `domain` (el núcleo de la cebolla), `data`, `presentation`, `service`, `di` y `core`.
- Se aplicó estrictamente la Inversión de Dependencias (Regla de Dependencias Hacia Adentro propia de la arquitectura Onion): Las capas de `data` (Room), `presentation` (MVVM) y `service` (Workers) conocen a `domain` interactuando únicamente a través de sus interfaces, pero `domain` no conoce absolutamente nada de ellas, logrando así el desacoplamiento exigido.
- Para evitar acoplamientos sutiles incluso con frameworks de concurrencia rígidos, se decidió implementar la inyección de despachadores de corrutinas (`@IoDispatcher`) en un módulo agnóstico (`core`). Esto permite que, aunque las capas externas apunten al centro, el centro siga siendo 100% testeable sin ataduras.

### 2. Construcción del Núcleo de Dominio (Fase 1)
**Instrucción proporcionada:** 
> "Crea la capa de dominio. Necesitamos los modelos puros para representar el punto GPS, el dispositivo BLE y el evento combinado. También define las interfaces para los repositorios de sensores, sincronización y control de rastreo. Finalmente, extrae toda la lógica de negocio en Casos de Uso (Use Cases) independientes para aplicar el principio de Responsabilidad Única (SRP)."

**Resultado y Decisiones Adoptadas:**
- **Modelos Puros:** Se crearon los data classes `GeoPoint`, `BleDevice` y `SensorEvent` como simples clases Kotlin, garantizando que el dominio desconozca la existencia de la base de datos o de las anotaciones de Room (`@Entity`).
- **Inversión de Dependencias (Contratos):** Se definieron interfaces clave como `SensorRepository` y `SyncRepository`. Esto asegura que los casos de uso dependan de abstracciones y no de implementaciones (Room/Retrofit).
- **Casos de Uso (Use Cases):** Se decidió orquestar toda la lógica a través de casos de uso granulares, inyectables y testeables de manera aislada:
  - `ObserveCombinedSensorDataUseCase`: Para combinar la lógica asíncrona de los flujos de GPS y BLE.
  - `RecordSensorEventsUseCase`: Para guardar los eventos en la base de datos.
  - `StartTrackingUseCase` / `StopTrackingUseCase`: Para abstraer el inicio y fin del servicio.
  - `ObserveTrackingStateUseCase` / `GetLastEventsUseCase`: Para alimentar el ViewModel con información reactiva de solo lectura.

**Reflexión Final de la Fase:**
Con esta base de dominio sólidamente estructurada, garantizamos que las reglas de negocio de la aplicación (que el reto define como críticas para la analítica empresarial) sean agnósticas a los cambios en la UI, el sistema operativo o el método de almacenamiento.
