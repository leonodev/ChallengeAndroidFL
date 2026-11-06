# AI Prompt History & Architecture Confirmation

## Instrucciones y Acuerdos de Colaboración
1. **Historial en `.ai-history/`**: Guardar los prompts y decisiones clave en archivos `.md` estructurados y limpios para reflejar la colaboración asistida por IA.
2. **Commits por Fase**: Realizar un commit por cada fase completada para mantener un historial limpio y legible para el revisor (`feat(domain): ...`, `feat(flows): ...`, etc.).
3. **Separación Estricta de Capas**:
   - **Capa de Dominio (`domain/`)**: Modelos puros de Kotlin (`GeoPoint`, `BleDevice`, `SensorEvent`), interfaces de repositorios (`SensorRepository`, `SyncRepository`), y Use Cases. **Cero dependencias de Android o Room**.
   - **Capa de Datos (`data/`)**: Implementaciones de repositorios, entidades y DAOs de Room (`SensorEntity`, `SensorDao`), mappers de Entity ↔ Domain, y fuentes de datos remotas/mock.

