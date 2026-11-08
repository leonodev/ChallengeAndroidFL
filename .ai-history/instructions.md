# Mobile Senior Developer Technical Lab: GeoBeacon Tracker (Refined)

## 1. Overview
Welcome to the technical evaluation for the Senior Mobile Developer and Team Leader position. 

In our organization, we rely heavily on continuous sensor data collection to drive enterprise geolocation analytics. To ensure this lab respects your time and fits realistically within a **4-hour window**, we have removed the heavy boilerplate required for Android OS constraints (Foreground Services, Room Database setup, and hardware permissions). 

**Your Mission:** Focus entirely on architectural plumbing. You will implement the domain and data layers to capture mocked BLE devices, combine them with GPS coordinates, cache them locally, and schedule a mock network sync.

## 2. Functional Requirements
* **Provided for you (in the boilerplate):** 
  * The `TrackingService` class that keeps the app alive in the background.
  * Room Database skeleton (`AppDatabase`, `SensorDao`, `SensorEntity`).
  * Hardware Abstraction layers: `LocationTracker` (Flow of GPS coordinates) and `DeviceScanner` (Flow of BLE devices). Both come with Mock implementations injected via Hilt.
* **To be Implemented by You:**
  * **Data Streams:** Combine the stream of GPS coordinates with the stream of BLE scanning results using Kotlin Flows.
  * **Data Storage:** Save the combined data locally using the provided Room DAO. 
  * **Network Sync (Mocked):** Create a repository that simulates uploading this batched data to a remote server every 15 minutes. *Note: A simple Coroutine `delay()` loop in a Singleton Repository is perfectly acceptable here—no need to over-engineer with WorkManager unless preferred.*
  * **Minimal UI Integration:** Connect a simple "Start/Stop Tracking" toggle to your ViewModel and display the last 10 logged events in a basic list.

## 3. Non-Functional & Architectural Requirements
We evaluate *how* you build, not just if it works:
* **Architecture:** Use MVVM or MVI with Clean Architecture principles.
* **Concurrency:** Heavy use of Kotlin Coroutines and Flows (e.g., `combine`, `zip`, or `flatMapLatest`) for merging asynchronous sensor data streams.
* **Testing:** Provide unit tests for your ViewModels or Domain Use Cases.

## 4. Deliverables & Submission Guidelines
Please submit your completed lab via a public GitHub or GitLab repository link. 

Your repository **must** include the following:
1. **The Android Project:** Complete with the source code.
2. **README.md:** 
   * Instructions on how to build and run the app.
   * A brief explanation of your architectural choices, specifically how you handled combining the Kotlin Flows.
   * Trade-offs: What would you improve if this were going to production and you had more time?
3. **AI Collaboration Logs:** We embrace AI-assisted development. Create a folder named `.ai-history/` and include the transcripts or prompts from any AI tools you used. We evaluate *how* you prompt and collaborate with AI.

## 5. Evaluation Criteria
We will review your submission based on:
* **Concurrency Skills:** Elegant handling of multiple asynchronous data streams using Kotlin Flows.
* **Separation of Concerns:** Clear boundaries between UI, domain, and data layers.
* **Code Quality:** Is the code clean, modular, and easy for a junior developer to read?
