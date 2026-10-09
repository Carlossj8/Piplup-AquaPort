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

### Reto 04: Principios SOLID (SRP y DIP)

Se rediseñó el núcleo del sistema aplicando el Principio de Responsabilidad Única (SRP) y el Principio de Inversión de Dependencias (DIP) para eliminar el acoplamiento y la acumulación de responsabilidades.

Aspectos implementados:
* Segregación de responsabilidades mediante clases especializadas: RegistradorMisiones coordina el flujo, ValidadorMision encapsula las reglas de negocio, y NotificadorOperador gestiona la comunicación con el usuario.
* Definición de la interfaz RepositorioMisiones en la capa de dominio, aislando la lógica de negocio de los detalles de almacenamiento.
* Implementación en memoria RepositorioMisionesMemoria ubicada en infraestructura.
* Inyección de dependencias por constructor en RegistradorMisiones, permitiendo desacoplamiento total y facilitando el uso de dobles de prueba.
* Pruebas unitarias que verifican validaciones, persistencia, manejo de identificadores duplicados y emisión de notificaciones.

![Diagrama de Clases Reto 04](docs/img/reto4.png)

### Reto 05: Diagrama de Contexto C4 (Nivel 1)

Se elaboró el diagrama de contexto C4 correspondiente al nivel 1 para delimitar las fronteras del sistema AquaPort MVP, identificando a los usuarios externos y los flujos de información sin exponer detalles internos de implementación.

Aspectos modelados:
* Sistema central: AquaPort MVP como sistema autónomo de supervisión y gestión de drones acuáticos en el campus de la Escuela Colombiana de Ingeniería.
* Actor Operador Hídrico: Encargado de registrar misiones, consultar la disponibilidad de drones y asignar unidades de forma manual.
* Actor Solicitante: Investigador o miembro del campus que genera requerimientos de transporte de muestras de agua y sensores.
* Actor Administrador ECI: Rol directivo que consulta métricas, reportes de desempeño y estado consolidado de la flota.
* Delimitación del MVP: Ausencia de integraciones con sistemas externos en esta primera fase.
* Relaciones etiquetadas: Definición explícita de los datos y solicitudes que viajan entre cada actor y el sistema central.

![Diagrama de Contexto Reto 05](docs/img/reto5.png)


