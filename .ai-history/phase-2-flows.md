# Phase 2: Combinación de Flujos

## Decisiones Técnicas y Justificación (para el README)
1. **Operador Elegido (`flatMapLatest`)**:
   - Se combinó el flujo de escaneo BLE (`DeviceScanner`) con el flujo de ubicación (`LocationTracker`) utilizando `flatMapLatest` junto con `.take(1)`.
   - *Justificación*: Cada vez que se detecta un dispositivo BLE, se toma la última posición GPS conocida. Esto evita duplicar registros innecesariamente (lo que ocurriría con un `combine` puro ante cada leve cambio de GPS) y garantiza un evento preciso por cada escaneo BLE enriquecido con la geolocalización actual.
2. **Control de Presión de Datos (Backpressure)**:
   - Se aplicaron los operadores `.buffer()` y `.conflate()` para manejar ráfagas de emisiones de dispositivos BLE y evitar la saturación del hilo de procesamiento.
3. **Inyección de Dispatchers (`@IoDispatcher`)**:
   - Se crearon los calificadores Hilt `@IoDispatcher` y `@MainDispatcher` en `CoroutinesModule`, inyectando `Dispatchers.IO` en el Use Case para facilitar las pruebas unitarias con `TestDispatcher`.

## Verificación
- Compilación exitosa (`app:assembleDebug`).
