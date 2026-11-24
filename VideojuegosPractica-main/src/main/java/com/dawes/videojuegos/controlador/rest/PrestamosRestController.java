package com.dawes.videojuegos.controlador.rest;

import com.dawes.videojuegos.DTO.PrestamosDTO;
import com.dawes.videojuegos.service.PrestamosService;
import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;

@RestController
@RequestMapping("/api")
@Tag(name = "Préstamos", description = "Gestión de préstamos de videojuegos: consultas, altas y devoluciones")
public class PrestamosRestController {
	private final PrestamosService prestamosService;

	public PrestamosRestController(PrestamosService prestamosService) {
		this.prestamosService = prestamosService;
	}

	@Operation(summary = "Listar préstamos",
			description = "Devuelve los préstamos filtrados por socio, título de videojuego, estado "
					+ "(EN_PLAZO, VENCIDO, DEVUELTO) y fecha de préstamo desde. "
					+ "Los filtros son opcionales y combinables.")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Lista de préstamos (puede estar vacía)",
					content = @Content(schema = @Schema(implementation = PrestamosDTO.class))),
			@ApiResponse(responseCode = "400", description = "Estado de filtro no válido")
	})
	@GetMapping("/prestamos")
	public ResponseEntity<ArrayList<PrestamosDTO>> listarPrestamos(
			@Parameter(description = "Texto contenido en el nombre o apellidos del socio")
			@RequestParam(required = false) String socio,
			@Parameter(description = "Texto contenido en el título del videojuego")
			@RequestParam(required = false) String titulo,
			@Parameter(description = "Estado del préstamo: EN_PLAZO, VENCIDO o DEVUELTO", example = "EN_PLAZO")
			@RequestParam(required = false) String estado,
			@Parameter(description = "Fecha de préstamo desde (inclusive), formato ISO yyyy-MM-dd", example = "2026-01-01")
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaDesde)
			throws SQLException {
		return ResponseEntity.ok(prestamosService.listarPrestamos(socio, titulo, null, estado, fechaDesde));
	}

	@Operation(summary = "Obtener préstamo por ID",
			description = "Devuelve un préstamo con su estado calculado "
					+ "(EN_PLAZO, VENCIDO o DEVUELTO) y los días de retraso.")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Préstamo encontrado",
					content = @Content(schema = @Schema(implementation = PrestamosDTO.class))),
			@ApiResponse(responseCode = "404", description = "No existe un préstamo con ese ID")
	})
	@GetMapping("/prestamos/{id}")
	public ResponseEntity<PrestamosDTO> obtenerPrestamo(
			@Parameter(description = "ID del préstamo", example = "17")
			@PathVariable int id) throws SQLException {
		return ResponseEntity.ok(prestamosService.buscarPrestamoPorId(id));
	}

	@Operation(summary = "Registrar un préstamo",
			description = "Crea un préstamo para un socio y una copia. Comprueba primero que el socio y la copia "
					+ "existen (404) y después aplica las reglas RN-1 a RN-5 en orden (422). "
					+ "La fecha límite es de 7 días para novedades (año actual o anterior) o 15 días en otro caso.")
	@ApiResponses({
			@ApiResponse(responseCode = "201", description = "Préstamo registrado",
					content = @Content(schema = @Schema(implementation = PrestamosDTO.class))),
			@ApiResponse(responseCode = "400", description = "Faltan idSocio o idCopia"),
			@ApiResponse(responseCode = "404", description = "El socio o la copia no existen"),
			@ApiResponse(responseCode = "422", description = "No se cumple una regla de negocio RN-1 a RN-5")
	})
	@PostMapping("/prestamos")
	public ResponseEntity<PrestamosDTO> registrarPrestamo(
			@io.swagger.v3.oas.annotations.parameters.RequestBody(
					description = "Datos del préstamo: idSocio e idCopia son obligatorios",
					required = true,
					content = @Content(schema = @Schema(implementation = PrestamosDTO.class)))
			@RequestBody(required = false) PrestamosDTO prestamo) throws SQLException {
		Integer idSocio = prestamo == null ? null : prestamo.getIdSocio();
		Integer idCopia = prestamo == null ? null : prestamo.getIdCopia();
		PrestamosDTO prestamoCreado = prestamosService.registrarPrestamo(idSocio, idCopia);
		return ResponseEntity.status(HttpStatus.CREATED).body(prestamoCreado);
	}

	@Operation(summary = "Devolver un préstamo",
			description = "Registra la devolución de un préstamo. Calcula la multa de 0,50 € por cada día de "
					+ "retraso y, si la copia se devuelve dañada, la marca como DAÑADO e inactiva en la misma transacción.")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Préstamo devuelto",
					content = @Content(schema = @Schema(implementation = PrestamosDTO.class))),
			@ApiResponse(responseCode = "404", description = "No existe un préstamo con ese ID"),
			@ApiResponse(responseCode = "409", description = "El préstamo ya estaba devuelto")
	})
	@PutMapping("/prestamos/{id}/devolucion")
	public ResponseEntity<PrestamosDTO> devolverPrestamo(
			@Parameter(description = "ID del préstamo a devolver", example = "17")
			@PathVariable int id,
			@Parameter(description = "Cuerpo opcional; admite copiaDañada o copiaDanada (boolean)")
			@RequestBody(required = false) JsonNode devolucion,
			@Parameter(description = "Indica si la copia se devuelve dañada")
			@RequestParam(required = false) Boolean copiaDanada,
			@Parameter(description = "Alternativa con ñ para indicar copia dañada")
			@RequestParam(required = false, name = "copiaDañada") Boolean copiaDanadaConEnie)
			throws SQLException {
		boolean danada = resolverCopiaDanada(devolucion, copiaDanada, copiaDanadaConEnie);
		return ResponseEntity.ok(prestamosService.devolverPrestamo(id, danada));
	}

	private boolean resolverCopiaDanada(JsonNode devolucion, Boolean copiaDanada, Boolean copiaDanadaConEnie) {
		if (copiaDanada != null) {
			return copiaDanada;
		}
		if (copiaDanadaConEnie != null) {
			return copiaDanadaConEnie;
		}
		if (devolucion == null || devolucion.isNull()) {
			return false;
		}
		JsonNode nodo = devolucion.path("copiaDañada");
		if (nodo.isMissingNode() || nodo.isNull()) {
			nodo = devolucion.path("copiaDanada");
		}
		return nodo.asBoolean(false);
	}
}

