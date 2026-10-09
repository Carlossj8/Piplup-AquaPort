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

### Reto 06: Requisitos Funcionales, No Funcionales y MoSCoW

Se definieron los requisitos nucleares para AquaPort MVP especificando el actor, la acción y el resultado observable para los funcionales, y métricas cuantitativas para los no funcionales.

#### Requisitos Funcionales (RF)
* RF-01 (Consultar disponibilidad de flota): El Operador Hídrico consulta la flota de drones para visualizar en pantalla el identificador, zona actual y nivel de batería de las unidades disponibles con carga mayor o igual al 35%.
* RF-02 (Registrar y asignar misión de transporte): El Operador Hídrico ingresa los puntos de origen y destino, tipo de carga y asigna manualmente un drone disponible, obteniendo como resultado un identificador único de misión y el registro persistido.
* RF-03 (Consultar estado de misión por identificador): El Solicitante introduce el identificador de su misión para conocer en tiempo real el estado de entrega y el drone responsable asignado.

#### Requisitos No Funcionales (RNF)
* RNF-01 (Tiempo de respuesta de consulta): La consulta de drones disponibles y ordenados por nivel de batería debe ejecutarse en menos de 200 milisegundos para una flota de hasta 10 drones, validado mediante JUnit 5 assertTimeout.
* RNF-02 (Integridad de datos y validación temprana): El sistema debe impedir el 100% de los intentos de registro que contengan campos nulos, puntos de ruta inválidos o drones con batería inferior al 35%, lanzando excepciones de negocio de forma inmediata.
* RNF-03 (Mantenibilidad y cobertura de pruebas): La capa de dominio y validación de misiones debe mantener una cobertura de líneas de código superior o igual al 80%, medida y auditada mediante JaCoCo en la fase de test.

#### Priorización MoSCoW

| Requisito | Tipo | Prioridad MoSCoW | Justificación |
| :--- | :--- | :--- | :--- |
| RF-01 | Funcional | Must Have | Indispensable para que el operador conozca las unidades operativas antes de autorizar cualquier despacho. |
| RF-02 | Funcional | Must Have | Constituye la funcionalidad nuclear de negocio del MVP para posibilitar el transporte de muestras en el campus. |
| RNF-02 | No Funcional | Must Have | Crítico para evitar que se despachen drones descargados o se almacenen registros inconsistentes en el sistema. |
| RF-03 | Funcional | Should Have | Importante para la trazabilidad y consulta por parte de los solicitantes, aunque no detiene la operación física de los drones. |
| RNF-01 | No Funcional | Should Have | Relevante para asegurar una experiencia de usuario ágil durante la consulta manual de la flota en el panel. |
| RNF-03 | No Funcional | Could Have | Conveniente para asegurar la calidad técnica del código base previo a su escalamiento hacia los niveles autónomos. |

### Reto 07: Plantilla DOSW (Especificación de Requerimiento)

Se documentó detalladamente la especificación formal del requerimiento funcional AP-01 utilizando el estándar de la plantilla institucional DOSW, alojada en el repositorio en `docs/word/Plantilla_Requerimientos_DOSW_EjercicioTHC.docx`.

Aspectos documentados en la plantilla:
* Código y nombre: AP-01 Registrar misión de transporte de muestra.
* Actor principal: Operador Hídrico.
* Precondición: Existencia en el sistema de al menos un drone acuático disponible con nivel de batería mayor o igual al 35%.
* Estructura de datos de entrada: Tipos de datos reales de dominio para drone asignado (DroneAcuatico), puntoPartida (String), puntoLlegada (String) y tipoCarga (Enum: MUESTRA_AGUA, SENSOR, PAQUETE_LIGERO), evitando elementos genéricos de interfaz gráfica.
* Datos de salida: Identificador alfanumérico generado para la misión y confirmación de persistencia.
* Flujo básico de eventos: Secuencia ordenada de 5 pasos desde la selección de parámetros, validación de disponibilidad, verificación de zonas hídricas hasta la confirmación al operador.
* Flujos alternos: Manejo de escenarios excepcionales para batería crítica del drone (<35%), zonas fuera de cobertura y coincidencia de origen y destino.
* Reglas de negocio: Exclusividad de misión activa por drone y validación obligatoria del umbral mínimo de carga.




