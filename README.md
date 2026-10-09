# AquaPort - Sistema de Drones Acuáticos de la ECI

Refuerzo de temáticas de desarrollo y arquitectura de software (DOSW). El proyecto implementa un sistema para la gestión, asignación y monitoreo de drones acuáticos en el campus de la Escuela Colombiana de Ingeniería, estructurado en tres niveles de evolución.

## Nivel 1: Piplup (MVP)

En esta primera etapa se construye la base funcional del sistema considerando una flota reducida de drones, asignación manual por parte del operador y zonas hídricas fijas.

### Reto 01: Streams y Lambdas

Se implementó el componente de consulta de flota mediante la API de Streams de Java 21, evitando estructuras iterativas tradicionales y variables de estado mutables.

Aspectos implementados:
* Definición del modelo DroneAcuatico como record inmutable para representar la información de cada unidad.
* Consulta de drones disponibles con batería mínima del 35%, ordenados de forma descendente por nivel de carga usando filter y sorted.
* Transformación de la colección para extraer únicamente los identificadores de los drones disponibles mediante map.
* Evaluación de existencia de unidades operativas aptas mediante anyMatch.
* Conteo de drones disponibles en la flota a través de count.
* Búsqueda del drone con mayor nivel de batería mediante max y Optional.
* Ejecución demostrativa en método main y validación automatizada mediante pruebas unitarias en JUnit 5.

![Evidencia Reto 01](docs/img/reto-01-streams.png)

### Reto 02: GitHub y GitFlow

Se configuró el flujo de trabajo colaborativo y control de versiones bajo el modelo GitFlow, asegurando trazabilidad y aislamiento de cambios.

Aspectos implementados:
* Configuración de archivo .gitignore excluyendo artefactos de compilación, carpetas de IDEs y dependencias locales.
* Estructura de ramas principales con rama main para versiones estables y rama develop para integración.
* Creación de la rama feature/streams-consultor-flota para el desarrollo del primer reto.
* Registro de commits atómicos con mensajes en presente y en español describiendo el alcance exacto de cada cambio.
* Integración de la funcionalidad en develop mediante Pull Request revisado y fusionado en GitHub.

![Evidencia Reto 02](docs/img/reto-02-gitflow.png)

### Reto 03: Patrones de Diseño - Builder

Se aplicó el patrón creacional Builder para la construcción controlada de instancias de la clase Mision, garantizando la integridad de los datos antes de permitir la existencia del objeto.

Aspectos implementados:
* Creación de los enums de dominio TipoCarga y EstadoMision.
* Ocultamiento del constructor de Mision haciéndolo privado, restringiendo la instanciación únicamente al Builder interno.
* Validación estricta en el método build para impedir identificadores, puntos de partida o de llegada nulos o en blanco.
* Regla de negocio que verifica que el drone asignado se encuentre en estado disponible antes de crear la misión.
* Asignación de estado por defecto PENDIENTE cuando no se suministra uno explícito.
* Pruebas unitarias en JUnit 5 que comprueban tanto la construcción exitosa como el lanzamiento de IllegalStateException en cada caso inválido.

![Evidencia Reto 03](docs/img/reto-03-builder.png)
