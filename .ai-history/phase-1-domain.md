# Phase 1: Domain Layer (The Core)

## Context and Objective
- The objective of this phase is to establish the core of the application by creating the business models, interfaces (contracts), and use cases, ensuring zero coupling with layers it should not interact with under a clean (Onion) architecture.
- Upon completing each and every instruction, you must verify and compile the project to ensure its stability after this phase.

### 1. Instructions for Defining Models and Contracts
> "Within the previously created domain layer, we need the pure models to represent the GPS point, the BLE device, and the combined event (SensorEvent). Also, define the interfaces for the repositories that will handle local sensor storage and synchronization with the remote server. Remember, none of these classes should import Android SDK components or database annotations."

**Expected Result:**
- **Pure Kotlin Models**:
  - `GeoPoint.kt`: Geographic coordinates (`latitude`, `longitude`) and timestamp.
  - `BleDevice.kt`: Detected BLE device (`macAddress`, `rssi`, `timestamp`).
  - `SensorEvent.kt`: Final combined event unifying BLE and GPS.
- **Contracts (Dependency Inversion)**:
  - `SensorRepository.kt`: Interface defining insertion operations, recent event observation via `Flow`, query of unsynchronized records, and success marking.
  - `SyncRepository.kt`: Pure interface to orchestrate the synchronization of a `SensorEvent` list.
  - `DeviceScanner.kt` and `LocationTracker.kt`: Interfaces representing the abstracted hardware flows.
  - `TrackingController.kt`: Interface exposing a `StateFlow` of the tracking state and actions to start/stop, thus decoupling the service layer from the use cases.
 
### 2. Logic Extraction into Use Cases
**Provided Instruction:** 
> "Now, extract all business and orchestration logic into independent Use Cases to apply the Single Responsibility Principle (SRP) and achieve maximum cohesiveness and testability of the challenge's functional requirements."

**Expected Result:**
- Multiple independent, constructor-injectable UseCases:
  - `GetLastEventsUseCase`: Encapsulates the reactive fetching of the latest registered events.
  - `StartTrackingUseCase` and `StopTrackingUseCase`: Isolate the logic of turning the tracking system on and off through the `TrackingController` abstraction.
  - `ObserveTrackingStateUseCase`: Exposes the current service state to the UI.
  - `RecordSensorEventsUseCase` and `InsertSensorEventUseCase`: Abstractions to orchestrate the flow towards the repository.