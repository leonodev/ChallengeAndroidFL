# Fase 3: Capa de Datos (Persistencia y Sincronización)

## Contexto y Objetivo
- El objetivo de esta fase es implementar la persistencia local robusta usando Room para almacenar los eventos de sensores combinados, y establecer un mecanismo asíncrono para su posterior sincronización periódica hacia un servidor remoto mediante WorkManager.
- Al finalizar todas y cada una de las instrucciones, debes verificar y compilar el proyecto para garantizar su estabilidad tras finalizar esta fase.

## Instrucción proporcionada:
> "En la capa de datos (data), implementa la base de datos local usando Room. Necesitamos una entidad que represente el SensorEvent, un DAO con consultas para guardar, obtener los últimos 10, listar los no sincronizados. Crea Mappers para aislar el dominio de las entidades. Luego, implementa el repositorio local `SensorRepositoryImpl` inyectando coroutines para no bloquear el hilo principal."

**Resultado esperado:**
- **Entidad y DAO de Room**:
   - Creación de `SensorEntity` con los campos necesarios y un flag adicional `synced: Boolean = false`.
   - Creación de `SensorDao` con anotaciones puras de Room (`@Insert`, `@Query`) exponiendo coroutines (`suspend`) y flujos reactivos (`Flow<List<SensorEntity>>`).
- **Mappers (`SensorMapper.kt`)**: 
   - Funciones de extensión `toDomain()` y `toEntity()` para transformar entre `SensorEntity` de Room y `SensorEvent` del dominio, protegiendo así el núcleo.
- **Repositorio (`SensorRepositoryImpl`)**:
   - Implementación estricta de la interfaz `SensorRepository`, actuando de puente mediante el DAO y mapeando los resultados antes de emitirlos.

### 2. Sincronización Periódica (Background Work)
**Instrucción proporcionada:** 
> "Para cumplir el requerimiento de enviar los datos al servidor (mock) periódicamente cada 15 minutos, implementaremos una capa de servicios (`service`). Aunque un simple loop con delay es aceptable, utilizaremos `WorkManager` para demostrar alineación con los estándares modernos de Android, ya que permite definir restricciones de red y sobrevivir a reinicios de la aplicación."

**Resultado esperado:**
- **WorkManager y Sincronización (`SyncWorker.kt`)**:
   - Creación de un `CoroutineWorker` inyectado mediante Hilt (`@HiltWorker`, `@AssistedInject`).
   - El Worker solicita los eventos no sincronizados al `SensorRepository`, los sube mediante `SyncRepository`, y solo tras un éxito, los marca masivamente como sincronizados.
- **Orquestador (`SyncScheduler.kt`)**:
   - Clase encargada de encolar un `PeriodicWorkRequest` de 15 minutos, asegurando que se respete la restricción de que exista conexión a internet (`NetworkType.CONNECTED`).
- **Inyección de Dependencias**: 
   - Modificación de Hilt en los módulos `AppModule.kt` y configuración de Factory en `GeoBeaconApp.kt` para orquestar correctamente a los Workers.
