# Phase 3: Data Layer (Persistence and Synchronization)

## Context and Objective
- The objective of this phase is to implement robust local persistence using Room to store the combined sensor events, and to establish an asynchronous mechanism for their subsequent periodic synchronization to a remote server using WorkManager.
- Upon completing each and every instruction, you must verify and compile the project to ensure its stability after this phase.

## Provided Instruction:
> "In the data layer, implement the local database using Room. We need an entity representing the SensorEvent, and a DAO with queries to save, get the latest 10, and list unsynchronized ones. Create Mappers to isolate the domain from the entities. Then, implement the local repository `SensorRepositoryImpl` injecting coroutines to avoid blocking the main thread."

**Expected Result:**
- **Room Entity and DAO**:
   - Creation of `SensorEntity` with the necessary fields and an additional flag `synced: Boolean = false`.
   - Creation of `SensorDao` with pure Room annotations (`@Insert`, `@Query`) exposing coroutines (`suspend`) and reactive flows (`Flow<List<SensorEntity>>`).
- **Mappers (`SensorMapper.kt`)**: 
   - Extension functions `toDomain()` and `toEntity()` to transform between Room's `SensorEntity` and the domain's `SensorEvent`, thus protecting the core.
- **Repository (`SensorRepositoryImpl`)**:
   - Strict implementation of the `SensorRepository` interface, acting as a bridge via the DAO and mapping the results before emitting them.

### 2. Periodic Synchronization (Background Work)
**Provided Instruction:** 
> "To fulfill the requirement of sending the data to the (mock) server periodically every 15 minutes, we will implement a services layer (`service`). Although a simple loop with delay is acceptable, we will use `WorkManager` to demonstrate alignment with modern Android standards, as it allows defining network constraints and surviving application restarts."

**Expected Result:**
- **WorkManager and Synchronization (`SyncWorker.kt`)**:
   - Creation of a Hilt-injected `CoroutineWorker` (`@HiltWorker`, `@AssistedInject`).
   - The Worker requests unsynchronized events from the `SensorRepository`, uploads them via the `SyncRepository`, and only upon success, bulk marks them as synchronized.
- **Orchestrator (`SyncScheduler.kt`)**:
   - Class responsible for enqueuing a 15-minute `PeriodicWorkRequest`, ensuring the internet connection constraint (`NetworkType.CONNECTED`) is respected.
- **Dependency Injection**: 
   - Modification of Hilt in the `AppModule.kt` modules and Factory configuration in `GeoBeaconApp.kt` to correctly orchestrate the Workers.
