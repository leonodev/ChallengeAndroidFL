# Phase 2: Flow Combining (Solving GPS Cold Start)

## Context and Objective
- The objective of this phase is to implement the application's core logic: combining the continuous BLE hardware flow (DeviceScanner) with the GPS hardware flow (LocationTracker) to create combined sensor events, overcoming the physical limitations of real-world hardware asynchrony.
- Upon completing each and every instruction, you must verify and compile the project to ensure its stability after this phase.

## Provided Instruction:
> "Create the detailed implementation of the `ObserveCombinedSensorDataUseCase`. Keep in mind the main architectural challenge: the BLE hardware scans and emits immediately, but the GPS hardware may take several seconds to turn on and emit its first coordinate (GPS Cold Start). If we use operators like `combine` or block the BLE flow waiting for the GPS, we will saturate the system and lose BLE frames. Use a `channelFlow` with a local in-memory buffer to store the temporary BLE devices and emit them all at once when the first GPS fix arrives, thus achieving zero data loss."

**Expected Result:**
- **`ObserveCombinedSensorDataUseCase` Implementation**:
  - Use of `channelFlow` to handle multiple concurrent collections (`collect`) within the same output flow.
  - Temporary storage of BLE emissions (`mutableListOf<BleDevice>`) while the GPS does not yet have a valid location (cold start).
  - Flushing the buffer to the flow's output (database) the exact moment the first valid GPS coordinate arrives.
  - Real-time emission of subsequent devices always using the most recent GPS location.
