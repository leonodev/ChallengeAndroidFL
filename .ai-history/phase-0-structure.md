# Phase 0: Discovery and Preparation (Base Architecture)

## Context and Objective
- In this initial phase, the objective is to analyze the base code provided in the challenge (Base Room DAO, mocked `LocationTracker`, and `DeviceScanner`) and establish the project's architectural foundations before writing the implementation logic.
- Upon completing each and every instruction, you must verify and compile the project to ensure its stability after this phase.

## Provided Instruction:
> "Analyze the project's boilerplate; we will use Clean Architecture with MVVM. We need to define a clear package structure aligned with what a true Clean Architecture is, where the layers always point towards the inner layers and do not depend on external ones. Create the base directory structure segregated by layers (domain, data, presentation, service, di, core) and their corresponding subdirectories (models, repositories, usecases, local, remote, etc.). Additionally, you must create an agnostic module called core, where we might house some abstractions to avoid potential coupling."

**Expected Result:**
- **Upon completion, you must have created a Package structure like the following**:
  - `domain/models/`, `domain/repositories/`, `domain/usecases/`
  - `data/local/`, `data/remote/`, `data/repository/`, `data/mapper/`
  - `presentation/`, `di/`, `core/`, `service/`
- **Cross-cutting module (`core`)**: A `core` package was defined to host agnostic utilities that are cross-cutting to the entire application. It currently contains `Qualifiers.kt` (`@IoDispatcher`, `@MainDispatcher`), which is a best practice in Hilt for injecting `CoroutineDispatcher`. This ensures that execution threads are not "hardcoded" into repositories or services, allowing them to be easily replaced by `TestDispatchers` during unit testing.

