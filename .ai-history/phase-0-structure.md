# Fase 0: Reconocimiento y Preparación (Arquitectura Base)

## Contexto y Objetivo
- En esta fase inicial, el objetivo es analizar el código base proporcionado en el challenge (Room DAO base, `LocationTracker` y `DeviceScanner` simulados) y sentar las bases arquitectónicas del proyecto antes de escribir la lógica de implementación.
- Al finalizar todas y cada una de las intrucciones, debes verificar y compilar el proyecto para garantizar su estabilidad tras finalizar esta fase


## Instrucción proporcionada:
> "Analiza el boilerplate del proyecto, vamos a utilizar Clean Architecture con MVVM. Necesitamos definir una estructura de paquetes clara y alineada con lo que es realmente una arquitectura Clean, donde las capas apunten siempre hacia las capas internas y que no dependan de capaz externas. Crea la estructura base de directorios segregada por capas (domain, data, presentation, service, di, core) y sus subdirectorios correspondientes (models, repositories, usecases, local, remote, etc.). Además, debes crear un modulo agnóstico llamado core, donde es posible que alojemos algunas abstracciones para evitar posibles acoplamientos."

**Resultado esperado:**
- **Al finalizar deberas haber creado una estructura de Paquetes como la siguiente**:
  - `domain/models/`, `domain/repositories/`, `domain/usecases/`
  - `data/local/`, `data/remote/`, `data/repository/`, `data/mapper/`
  - `presentation/`, `di/`, `core/`, `service/`
- **Módulo transversal (`core`)**: Se definió un paquete `core` para alojar utilidades agnósticas y transversales a toda la aplicación. Actualmente contiene `Qualifiers.kt` (`@IoDispatcher`, `@MainDispatcher`), lo cual es una mejor práctica en Hilt para inyectar `CoroutineDispatcher`. Esto garantiza que los hilos de ejecución no estén "hardcodeados" en los repositorios o servicios, permitiendo sustituirlos fácilmente por `TestDispatchers` durante las pruebas unitarias.

