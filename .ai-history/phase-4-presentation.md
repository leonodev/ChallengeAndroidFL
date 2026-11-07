# Phase 4: Presentation and Foreground Service

## Context and Objective
- The objective of this phase is to connect the domain core (and its data engine) with the user interface, ensuring that the application state is reactive and that the tracking service can be kept alive in the background while respecting modern Android policies.
- Upon completing each and every instruction, you must verify and compile the project to ensure its stability after this phase.

## Provided Instruction: 
> "Implement the presentation layer. Create the `TrackingViewModel` to manage the state using `StateFlow`. Use the `combine` operator to merge the tracking service state emission and the real-time reading of the latest events from the database (Room). Make sure to expose mapped UI events and to manage user actions centrally."

**Expected Result:**
- **ViewModel (`TrackingViewModel`)**:
   - Implementation of a unidirectional data flow (Single Source of Truth) combining `observeTrackingStateUseCase()` and `getLastEventsUseCase()`.
   - Use of `SharingStarted.WhileSubscribed(5000)` to pause database reading when the app is in the background, saving battery.
   - Creation of encapsulated UI states (`TrackingUiState`) and mapped models ready to be displayed (`SensorEventUiModel`).
   - Abstraction of user intents through the sealed class `TrackingAction`.

### 2. Foreground Service (TrackingService)
**Provided Instruction:** 
> "Create the `TrackingService` that inherits from Android's Service. This service must run in the Foreground to keep the application alive. Its only responsibility should be to start the service, initiate tracking using the `RecordSensorEventsUseCase` use case, and start the `SyncScheduler` WorkManager orchestrator. Keep the service decoupled from complex logic."

**Expected Result:**
- **Foreground Service (`TrackingService`)**:
   - Configuration of a *Foreground Service* with a notification channel and explicit permissions (`FOREGROUND_SERVICE_TYPE_LOCATION`).
   - Orchestration of Hilt-injected dependencies (`@AndroidEntryPoint`).
   - Management of its own `CoroutineScope` coupled to the service's lifecycle.
   - Cross-starting of local tracking (`recordSensorEventsUseCase()`) and the cloud sync scheduler (`syncScheduler.startSyncWork()`).
