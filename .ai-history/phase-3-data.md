# Phase 3: Capa de Datos

## Implementación
1. **Entidad y DAO de Room**:
   - `SensorEntity`: Añadido campo `synced: Boolean = false`.
   - `SensorDao`: Consultas para obtener eventos no sincronizados (`WHERE synced = 0`) y marcar como sincronizados (`UPDATE sensor_data SET synced = 1 WHERE id IN (:ids)`).
   - `AppDatabase`: Versión incrementada a 2 con `.fallbackToDestructiveMigration()`.
2. **Mappers**:
   - `SensorMapper.kt`: Funciones de extensión `toDomain()` y `toEntity()` para transformar entre `SensorEntity` y `SensorEvent`.
3. **Repositorio (`SensorRepositoryImpl`)**:
   - Implementación de `SensorRepository` utilizando `SensorDao`, con inyección de `@IoDispatcher` y ejecución asíncrona mediante `withContext` y `flowOn`.
4. **Inyección de Dependencias y Scopes**:
   - Enlace `@Binds` en Hilt para `SensorRepository`.
   - Proveedor de `CoroutineScope` (`SupervisorJob` + `@IoDispatcher`) con `@ApplicationScope` para procesos de larga duración (como el sync loop de la Fase 4).

## Verificación
- Compilación exitosa (`app:assembleDebug`).
