# GameVault · Biblioteca de videojuegos

Aplicación de gestión para el personal de una biblioteca municipal que presta videojuegos físicos
(práctica de *Desarrollo Web en Entorno Servidor*, DAW).

## Tecnologías

- Java 25, Spring Boot 4, Thymeleaf
- JDBC con `DataSource` (sin JPA)
- MySQL 8

## Cómo ejecutarla

1. Ejecuta el script `biblioteca_videojuegos.sql` en MySQL (crea la base de datos `biblioteca` con datos de ejemplo).
2. Revisa usuario y contraseña en `src/main/resources/application.properties`.
3. Arranca la aplicación: `./mvnw spring-boot:run`
4. Abre <http://localhost:8080>

## Arquitectura en capas

`DTO → Repository → DAO → Service → Controller`

| Capa | Paquete | Responsabilidad |
|------|---------|-----------------|
| DTO | `DTO` | Objetos que viajan entre capas |
| Repository | `Repository` | Solo SQL, con `PreparedStatement` y parámetros |
| DAO | `interfaz` + `impl` | Interfaz `IxxxDAO` e implementación que delega en el Repository |
| Service | `service` | Validaciones y reglas de negocio |
| Controller | `controlador` | Recoge datos, llama al Service y prepara el modelo |

Los errores esperables (validaciones) se lanzan como `ValidacionException` desde el Service
y se muestran en la propia página.

## Funcionalidades implementadas

- **RF-01** Listado de videojuegos con filtros opcionales y combinables (título, desarrolladora, año desde, PEGI máximo, solo con copias disponibles)
- **RF-02** Alta de videojuego con validaciones
- **RF-08** Inicio con los contadores (videojuegos, socios activos, préstamos abiertos y vencidos)
