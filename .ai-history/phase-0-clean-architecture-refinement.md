# Refinamiento Arquitectónico (Clean Architecture Estricta)

## Mejoras Aplicadas
1. **Dominio 100% Puro (Cero Dependencias de DI)**:
   - Se removió la importación de qualifiers de Hilt (`di`) desde la capa de dominio. Los anotadores de Qualifiers (`@IoDispatcher`, `@MainDispatcher`, `@ApplicationScope`) se movieron al paquete neutral `com.geobeacon.tracker.core`.
2. **Unificación de Modelos de Ubicación**:
   - Se unificó `LocationData` y `GeoPoint`, estableciendo `GeoPoint` como el modelo puro oficial en `domain/models/` utilizado tanto por `LocationTracker` como por los casos de uso.
3. **Separación de Responsabilidades en Entidades**:
   - Se removió el concepto de persistencia `synced` del modelo de dominio `SensorEvent`. El estado de sincronización vive exclusivamente en `SensorEntity` (Capa de Datos).

## Verificación
- Compilación exitosa (`app:assembleDebug`).
- Pruebas unitarias pasando al 100% (`app:testDebugUnitTest`: 2 passed, 0 failed).
