# GeoBeacon Tracker

GeoBeacon Tracker is an Android application developed in **Kotlin** following strict **Clean Architecture** principles. It is designed for background capturing of GPS location and BLE (Bluetooth Low Energy) device scanning, reactive local persistence using Room, and guaranteed periodic synchronization with **WorkManager**.

---

## 🤖 Development History (AI Collaboration)

- To demonstrate the mature use of AI assistants in the development and architecture process, the exact prompts and guidelines that dictated the evolution of this project (divided into 6 phases) have been documented and are available for evaluation in the `.ai-history/` root directory.

## 🗺️ Roadmap (Execution Plan)
The development of this project was carried out through an initial analysis that resulted in a phase-by-phase development supported by AI. These phases and architectural justifications are reflected in the `00-project-roadmap.md` file, which is available in the `.ai-history/` root directory.

## 🚀 How to Build and Run

### Requirements
- **Android Studio** (Hedgehog or higher recommended).
- **JDK 17**.
- **Android SDK** (Compile SDK / Target SDK: 34, Min SDK: 26).

### Steps
1. Clone or open the project in Android Studio.
2. Sync the project with Gradle (`File > Sync Project with Gradle Files`).
3. Run the unit tests:
   ```bash
   ./gradlew testDebugUnitTest
   ```
4. Build and install the app on an emulator or physical device (API 26+):
   ```bash
   ./gradlew assembleDebug
   ```

---

## 🏛️ Architectural Decisions (Clean Architecture)

The project strictly respects the **Dependency Rule**, always pointing towards the center (Domain):

![Architecture Diagram](./docs/architecture.svg)

1. **Domain Layer (`domain/`)**:
   - Pure business models (`models/`: `GeoPoint`, `BleDevice`, `SensorEvent` composing `GeoPoint`).
   - Repository contracts (`repositories/`) and Use Cases (`usecases/`).
   - **Zero dependencies** on Android, Room, or Dependency Injection frameworks (Qualifiers reside in `core/`).

2. **Data Layer (`data/`)**:
   - Room Database (`AppDatabase`, `SensorDao`, `SensorEntity` managing the `synced` persistence state).
   - Bidirectional Mappers (`SensorMapper.kt`) to transform Entity ↔ Domain.
   - Repository implementations (`SensorRepositoryImpl`, `SyncRepositoryImpl`) and remote data source (`MockRemoteDataSource`).

3. **Presentation and Services Layer (`presentation/` and `service/`)**:
   - `TrackingViewModel` with reactive state `StateFlow` (`SharingStarted.WhileSubscribed(5000)`).
   - `TrackingService` (Foreground Service) configured with foreground service types for location (Android 14 API 34+) and integrated with WorkManager.

---

## 🌊 Flow Combining Justification (Hardware Management)

The main technical challenge when developing with real hardware is asynchrony. While the BLE scanner can emit bursts of devices almost immediately, the GPS sensor often requires several seconds to fix its first position (the well-known "GPS Cold Start").

To orchestrate this combination in the `ObserveCombinedSensorDataUseCase` use case:
- **The Solution (In-Memory Buffer)**: Instead of using standard operators like `combine` or `flatMapLatest`—which would lose fast BLE emissions while waiting for the GPS—I chose to use `channelFlow`, implementing a local buffer (`mutableListOf`). 
- **Zero Data Loss**: During the GPS cold start, detected BLE devices are not discarded, nor do they saturate the system threads (preventing *backpressure*); instead, they are accumulated in the buffer. The exact moment the GPS delivers the first valid coordinate, the entire buffer is flushed to the database (Room) assigning them that position, and then it continues emitting in real-time.
- **Asynchronous Testability**: To demonstrate the robustness of this decision, advanced unit tests were implemented using `runTest` and `Turbine`, simulating intentional latencies in the GPS simulated flow emission.

---

## 🔄 Synchronization with WorkManager

- **Why WorkManager**: To ensure that the synchronization of pending events with the server does not rely exclusively on manual time cycles and respects the device's state.
- **Implementation**: `SyncWorker` (annotated with `@HiltWorker` and `@AssistedInject`) processes pending Room records, uploads them via `SyncRepository`, and only marks the IDs as synchronized after a resounding success.
- **Configuration**: Single periodic task (`enqueueUniquePeriodicWork`) set to 15 minutes, with strict network constraints (`NetworkType.CONNECTED`) provided by the Android OS.

---

## ⚠️ Edge Cases and Scalability (Trade-offs)

Although the in-memory buffer elegantly resolves short cold starts (GPS Cold Starts of seconds or a few minutes), I am aware that in an extreme real-life scenario (e.g., for X minutes geolocation cannot be obtained while changing location, and the scanner keeps reading until a location is finally obtained), a serious problem and a geolocation false positive would be generated by assigning the final coordinate to boxes read half an hour ago.

For a **large-scale Production** environment, the following architectural solutions would be applied:
1. **Buffer Time-To-Live (TTL)**: Implement an age-based discard policy, removing from the buffer those BLE readings that are more than 2 minutes old without having been able to obtain a GPS fix.
2. **Agnostic Persistence (Nullable Location)**: Modify the business model to accept events with a null location (`val location: GeoPoint?`). This would allow the BLE reading to be immediately saved in Room and sent to the central server, allowing the company's algorithms to infer the position based on the vehicle's times and routes, without blocking the mobile data ingestion.
