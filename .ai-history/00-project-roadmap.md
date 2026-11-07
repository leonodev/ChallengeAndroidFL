# Execution Plan and Architectural Summary (GeoBeacon Tracker)

This document outlines the step-by-step development plan executed for the GeoBeacon Tracker technical challenge, reflecting the initial analysis, technical decisions made, and the code's evolution towards a high-quality standard.

---

## Read carefully and analyze the following instructions as a baseline for the technical decisions we will define
- **[Base instructions](instructions.md).**

## Context and Objective
- After analyzing the [base instructions](instructions.md), we will define the development in phases, allowing us to break it down into shorter, better-focused tasks.

## Analysis Summary and Resulting Phases with Technical Decisions

### Phase 0: Discovery and Preparation (Base Architecture)
- **Boilerplate Analysis**: Inspection of `LocationTracker`, `DeviceScanner`, base Room DAO, and background services.
- **Package Structure**: Strict adoption of Clean Architecture segregated by layers:
  - `domain/models/`, `domain/repositories/`, `domain/usecases/`
  - `data/local/`, `data/remote/`, `data/repository/`, `data/mapper/`
  - `presentation/`, `di/`, `core/`, `service/`
- **Concurrency Management**: Use of injected dispatchers (`@IoDispatcher`) to isolate threads and facilitate testing.

### Phase 1: Domain Layer (The Core)
- **Pure Models**: `GeoPoint`, `BleDevice`, `SensorEvent` (with no dependencies on the Android SDK, Room, or Retrofit).
- **Contracts (Dependency Inversion)**: Pure interfaces such as `SensorRepository`, `SyncRepository`, `TrackingController`, `DeviceScanner`, and `LocationTracker`.
- **Use Cases**: Extraction of all business logic into independent UseCases (`StartTrackingUseCase`, `StopTrackingUseCase`, `ObserveTrackingStateUseCase`, `GetLastEventsUseCase`, etc.) to achieve maximum cohesiveness and testability.

### Phase 2: Flow Combining and Resilience (Solving *GPS Cold Start*)
- **Chosen Strategy**: Use of `channelFlow` with an in-memory buffer (`mutableListOf<BleDevice>`) and parallel collection instead of standard operators.
- **Technical Justification**: Standard operators like `combine`, `zip`, or `flatMapLatest` are insufficient to handle hardware disparities (the GPS may take several seconds to get a fix, while the BLE fires immediately). Freezing the BLE flow while waiting for the GPS would generate *backpressure* and frame loss. By using a local buffer, we capture the entire BLE environment from millisecond zero and flush it to the database as soon as the first GPS *fix* arrives. Zero data loss.

### Phase 3: Data Layer (Persistence and Synchronization)
- **Room**: Use of the local database to store events. Implementation of bidirectional Mappers (`SensorMapper`) to isolate Room entities from the rest of the app.
- **Synchronization (Network Sync)**: Implementation of `SyncWorker` (WorkManager) orchestrated through `SyncScheduler`.
- **WorkManager Justification**: Although a `delay(15.minutes)` in a Foreground Service would suffice, using WorkManager ensures retry policies and network constraints (`NetworkType.CONNECTED`), showing alignment with modern Android standards.

### Phase 4: Presentation and Foreground Service
- **TrackingViewModel**: Reactive and unidirectional state management with `StateFlow` and `combine`, reacting to changes in the local database (Single Source of Truth) and the Foreground Service state.
- **TrackingService**: Decoupled from the UI; orchestrates the Use Cases and manages the persistent notification to keep the application alive in the background.

### Phase 5: Refactoring and Unit Testing (Ensuring Quality)
- **Continuous Refactoring**: Iteration on the architecture to extract service logic into Use Cases, ensuring the service acts purely as a lifecycle orchestrator.
- **Advanced Asynchronous Testing**: Extensive use of the `Turbine` library to test correct event emission, flow cancellations, and the proper injection of `TestDispatchers` without race conditions. Coverage of the *GPS Cold Start* scenario.

### Phase 6: Documentation and Delivery
- Thorough and honest documentation in `README.md`, detailing the architecture, the resolution of the hardware asynchrony issue, trade-offs (Doze Mode vs. Foreground Service timing), and conscious technical debt assumed to meet the estimated time.
