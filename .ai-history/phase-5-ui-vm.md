# Phase 5: ViewModel y UI Mínima

## Implementación
1. **TrackingViewModel**:
   - Expone `UiState` (`isTracking`, `lastEvents`, `error`) como `StateFlow` reactivo usando `stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ...)`.
   - Maneja acciones de usuario con patrón MVI ligero (`TrackingAction.ToggleTracking`).
2. **TrackingService**:
   - Configurado con `@AndroidEntryPoint`.
   - Inyecta `ObserveCombinedSensorDataUseCase`, `StartTrackingUseCase` y `SyncManager`.
   - Al iniciar (`onStartCommand`), arranca el loop de sincronización (`SyncManager.startSyncLoop()`) y recolecta el flujo combinado de sensores insertándolos en Room.
   - Detiene el servicio y el loop al recibir la acción `STOP_TRACKING`.
3. **MainActivity & ViewBinding**:
   - Botón Start/Stop que interactúa con el `ViewModel` y lanza/detiene el Foreground Service.
   - Vista reactiva que observa `uiState` mediante `repeatOnLifecycle(Lifecycle.State.STARTED)` y muestra en tiempo real los últimos eventos registrados.

## Verificación
- Compilación exitosa (`app:assembleDebug`).
