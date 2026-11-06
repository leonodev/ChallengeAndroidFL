# Phase 4: Sync Mock cada 15 min

## Implementación
1. **RemoteDataSource & MockRemoteDataSource**:
   - Interfaz y mock que simulan latencia de red (`delay`) y fallos aleatorios opcionales (10% de probabilidad de error de red).
2. **SyncRepositoryImpl**:
   - Implementa `SyncRepository` para coordinar el envío de lotes de eventos hacia el datasource remoto en hilos de IO.
3. **SyncManager**:
   - Singleton encargado de ejecutar el loop en segundo plano utilizando el `CoroutineScope` de aplicación inyectado (`@ApplicationScope`).
   - Lee lotes de eventos no sincronizados (`getUnsynchronizedEvents`), intenta subirlos, y si es exitoso, marca los registros como sincronizados (`markAsSynchronized`).
   - Intervalo configurable (por defecto 15 min, adaptable para testing/demo).
   - Manejo de reintentos simples ante fallos sin pérdida de datos (los registros se mantienen como no sincronizados).
4. **Hilt Modules**:
   - Bindings de `RemoteDataSource` y `SyncRepository` en `DomainModule`.

## Verificación
- Compilación exitosa (`app:assembleDebug`).
