# Phase 6: Tests

## Implementación
1. **Dependencias de Test**:
   - Añadido `kotlinx-coroutines-test`, `Turbine`, `MockK`, `junit` y `core-testing` en `app/build.gradle.kts`.
2. **ObserveCombinedSensorDataUseCaseTest**:
   - Tests con flujos fake y Turbine para verificar la combinación correcta del escaneo BLE y la última ubicación GPS conocida.
3. **TrackingViewModelTest**:
   - Tests con Turbine, MockK y `MainDispatcherRule` para verificar el estado inicial y la actualización reactiva de la lista de eventos.

## Verificación
- Pruebas unitarias ejecutadas con éxito (`app:testDebugUnitTest`: 2 passed, 0 failed).
