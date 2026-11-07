# Fase 2: Combinación de Flujos (Resolviendo el GPS Cold Start)

## Contexto y Objetivo
- El objetivo de esta fase es implementar la lógica central de la aplicación: combinar el flujo continuo de hardware BLE (DeviceScanner) con el flujo de hardware GPS (LocationTracker) para crear los eventos de sensores combinados, superando las limitaciones físicas de asincronía del hardware real.
- Al finalizar todas y cada una de las intrucciones, debes verificar y compilar el proyecto para garantizar su estabilidad tras finalizar esta fase.

## Instrucción proporcionada:
> "Crea la implementación detallada del caso de uso `ObserveCombinedSensorDataUseCase`. Ten en cuenta el reto arquitectónico principal: el hardware BLE escanea y emite de inmediato, pero el hardware GPS puede tardar varios segundos en encender y emitir su primera coordenada (GPS Cold Start). Si usamos operadores como `combine` o bloqueamos el flujo BLE esperando al GPS, saturaremos el sistema y perderemos tramas BLE. Utiliza un `channelFlow` con un buffer local en memoria para guardar los dispositivos BLE temporales y emitirlos de golpe cuando llegue el primer fix de GPS, logrando así cero pérdida de datos."

**Resultado esperado:**
- **Implementación del UseCase `ObserveCombinedSensorDataUseCase`**:
  - Utilización de `channelFlow` para manejar múltiples recolecciones (`collect`) concurrentes dentro de un mismo flujo de salida.
  - Almacenamiento temporal de emisiones BLE (`mutableListOf<BleDevice>`) mientras el GPS aún no tiene una ubicación válida (cold start).
  - Volcado (flush) del buffer hacia la salida del flujo (base de datos) en el instante en que llega la primera coordenada GPS válida.
  - Emisión en tiempo real de los dispositivos posteriores usando siempre la ubicación GPS más reciente.
