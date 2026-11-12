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
- **RF-02** Alta de videojuego con validaciones (título, año entre 1970 y el actual, PEGI válido)
- **RF-03** Listado de socios con filtros (nombre o apellidos, email, activo sí/no/todos), edad y préstamos abiertos
- **RF-04** Alta de socio (fecha de alta de hoy, activo, email único, datos obligatorios)
- **RF-05** Registro de préstamo con las reglas RN-1 a RN-5 en el Service y fecha límite según novedad (7 días) o normal (15 días)
- **RF-06** Listado de préstamos con filtros (socio, juego, estado, fecha desde) y estado calculado EN_PLAZO / VENCIDO / DEVUELTO con días de retraso
- **RF-07** Devolución de préstamos con multa de 0,50 €/día de retraso y opción de copia dañada, en una única transacción con `rollback`
- **RF-08** Inicio con los contadores (videojuegos, socios activos, préstamos abiertos y vencidos)

## Reglas de negocio (Service)

| Id | Regla |
|----|-------|
| RN-1 | El socio debe estar activo |
| RN-2 | La copia debe estar disponible (activa y sin préstamo abierto) |
| RN-3 | Un socio no puede tener más de 3 préstamos abiertos |
| RN-4 | La edad del socio debe ser mayor o igual que el PEGI del juego |
| RN-5 | Un socio con un préstamo vencido sin devolver no puede pedir otro |