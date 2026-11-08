# Phase 5: Refactoring and Unit Testing (Ensuring Quality)

## Context and Objective
- The objective of this phase is to consolidate the project's robustness by writing advanced unit tests for asynchronous flows and refactoring components to ensure that the service acts as a blind orchestrator and does not house business logic.
- Upon completing each and every instruction, you must verify and compile the project to ensure its stability after this phase.

## Provided Instruction:
> "Apply continuous refactoring to extract any residual logic from the TrackingService into Use Cases. Then, implement high-level asynchronous unit tests using the Turbine and MockK libraries. Be sure to inject `StandardTestDispatcher` and cover the complex GPS Cold Start scenario in the flow combining use case, and verify the reactive emission of states in the ViewModel."

**Expected Result:**
- **Continuous Refactoring**:
   - Creation of `RecordSensorEventsUseCase` and `InsertSensorEventUseCase` to extract the saving logic that originally resided in the Service, leaving it solely as an Android lifecycle orchestrator.
- **Asynchronous Testing (`ObserveCombinedSensorDataUseCaseTest`)**:
   - Implementation of tests with `runTest` simulating hardware flows with latencies (`delay(100)`).
   - Use of **Turbine** (`test { awaitItem() }`) to verify the correct combined emission and assert that there is no BLE packet loss if the GPS arrives late (GPS Cold Start).
   - Replacement of the natural closing operator with `cancelAndIgnoreRemainingEvents()` since hardware flows are inherently infinite.
- **Reactive Testing (`TrackingViewModelTest`)**:
   - Safe replacement of the Main Thread using `Dispatchers.setMain(StandardTestDispatcher())`.
   - Mocking of UseCases with **MockK** (`coEvery`) to simulate the emission of `SensorEvent` from Room.
   - Assertion of the initial state and UI state emissions using Turbine.
