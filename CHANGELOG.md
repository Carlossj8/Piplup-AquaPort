# Changelog — AquaPort

Todas las modificaciones notables de este proyecto están documentadas en este archivo siguiendo el formato de [Keep a Changelog](https://keepachangelog.com/es-ES/1.0.0/) y adhiriéndose al versionamiento semántico [SemVer](https://semver.org/lang/es/).

---

## [3.0.0-enterprise] — 2026-10-12 (Nivel 3: Empoleon)

### Added
- **Rutas Multi-Etapa y Waypoints:** Creación de las entidades `Waypoint` y `TramoRuta` en el dominio para coordinar misiones complejas con puntos de control intermedios.
- **CoordinadorRutasEnterprise:** Implementación de analítica avanzada con Streams para cálculo de eficiencia por zona, drone con más entregas exitosas, planificación de rutas multi-etapa y cálculo de carga con `flatMap`.
- **Collector Personalizado:** `agrupadorBateriaPromedioPorZona` para calcular la batería promedio por zona en una sola pasada con compatibilidad secuencial y paralela.
- **Git Hooks Enterprise:** Hook `commit-msg` para la validación obligatoria de Conventional Commits y trazabilidad del historial de cambios.
- **Políticas de Branch Protection:** Formalización de directrices para ramas protegidas `main` y `develop` con aprobación requerida y Quality Gate.

### Changed
- **Mision Entity:** Extensión del modelo para incorporar lista inmutable de `Waypoint` sin alterar el contrato de compatibilidad previa.

---

## [2.0.0] — 2026-10-09 (Nivel 2: Prinplup)

### Added
- **Flota Especializada:** Incorporación de 15 drones distribuidos en tres variantes especializadas (`SUPERFICIAL`, `SEMISUMERGIDO`, `BUCEADOR`) mediante Factory Method (`FabricaDrones`).
- **Patrón Strategy:** Selección autónoma de drones basada en políticas desacopladas (`MayorBateriaStrategy`, `PrioridadCriticaStrategy`).
- **Patrón Observer:** Despacho en tiempo real de eventos y alertas técnicas a observadores desacoplados (`CentroControlObserver`, `TecnicoMantenimientoObserver`).
- **AsignadorAutomatico:** Orquestador central de asignación con contingencia ante fallos técnicos y búsqueda autónoma de unidades alternativas.
- **ConsultorFlotaAvanzado:** Consultas avanzadas de Streams con agrupamientos, particionamiento de misiones críticas y métricas de telemetría.
- **TDD con Mockito:** Suite exhaustiva de pruebas unitarias sobre el `AsignadorAutomatico` cubriendo los 5 casos obligatorios y casos de borde.
- **JaCoCo Quality Gate:** Configuración en Maven con umbral estricto $\ge 80\%$ de cobertura de líneas y ramas.

### Changed
- **DroneAcuatico:** Adición de `TipoDrone` y `EstadoDrone` con constructores retrocompatibles.
- **Mision:** Adición de nivel de `Prioridad` con precedencia crítica.
- **Logger Centralizado:** Sustitución de flujos estándar por `java.util.logging.Logger` para eliminación de Code Smells.

---

## [1.0.0] — 2026-10-09 (Nivel 1: Piplup MVP)

### Added
- **Modelo Base:** Entidades de dominio `DroneAcuatico`, `Mision`, `TipoCarga` y `EstadoMision`.
- **Patrón Builder:** Construcción fluida y validada de instancias de `Mision`.
- **SOLID (SRP & DIP):** Separación de responsabilidades con `ValidadorMision`, interfaz `RepositorioMisiones` y servicio `RegistradorMisiones`.
- **ConsultorFlota:** Consultas iniciales mediante Java Streams para filtrado, conteo y ordenamiento de flota.
- **Pruebas TDD:** Ciclo Red-Green-Refactor para validación de reglas de negocio.
- **Integración de Calidad:** Configuración de JaCoCo al 100% en dominio y SonarCloud con Quality Gate Passed.
