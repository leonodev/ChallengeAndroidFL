# Fase 5: Refactor y Pruebas Unitarias (Asegurando la Calidad)

## Contexto y Objetivo
El objetivo de esta fase es consolidar la robustez del proyecto mediante la escritura de pruebas unitarias avanzadas para flujos asíncronos y refactorizar componentes para garantizar que el servicio actúe como un orquestador ciego y no albergue lógica de negocio.
Al finalizar todas y cada una de las instrucciones, debes verificar y compilar el proyecto para garantizar su estabilidad tras finalizar esta fase.

## Instrucción proporcionada:
> "Aplica una refactorización continua para extraer cualquier lógica residual del TrackingService hacia Casos de Uso. Luego, implementa pruebas unitarias asíncronas de alto nivel utilizando la librería Turbine y MockK. Asegúrate de inyectar `StandardTestDispatcher` y de cubrir el escenario complejo del GPS Cold Start en el caso de uso de combinación de flujos, y verifica la emisión reactiva de estados en el ViewModel."

**Resultado esperado:**
- **Refactorización Continua**:
   - Creación de `RecordSensorEventsUseCase` e `InsertSensorEventUseCase` para extraer la lógica de guardado que originalmente residía en el Servicio, dejándolo únicamente como orquestador del ciclo de vida de Android.
- **Testing Asíncrono (`ObserveCombinedSensorDataUseCaseTest`)**:
   - Implementación de pruebas con `runTest` simulando flujos de hardware con latencias (`delay(100)`).
   - Uso de **Turbine** (`test { awaitItem() }`) para verificar la emisión combinada correcta y afirmar que no hay pérdida de paquetes BLE si el GPS llega tarde (GPS Cold Start).
   - Sustitución del operador de cierre natural por `cancelAndIgnoreRemainingEvents()` ya que los flujos de hardware son inherentemente infinitos.
- **Testing Reactivo (`TrackingViewModelTest`)**:
   - Sustitución segura del Main Thread usando `Dispatchers.setMain(StandardTestDispatcher())`.
   - Mockeo de UseCases con **MockK** (`coEvery`) para simular la emisión de `SensorEvent` desde Room.
   - Afirmación del estado inicial y las emisiones de estado UI usando Turbine.
