package com.dawes.videojuegos.controlador.rest;

import com.dawes.videojuegos.DTO.PrestamosDTO;
import com.dawes.videojuegos.DTO.VideojuegoDTO;
import com.dawes.videojuegos.service.PrestamoNoEncontradoException;
import com.dawes.videojuegos.service.PrestamoYaDevueltoException;
import com.dawes.videojuegos.service.PrestamosService;
import com.dawes.videojuegos.service.ValidacionException;
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
@Tag(name = "Préstamos", description = "Gestión de préstamos de videojuegos: consultas, altas, renovaciones y devoluciones")
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
			@ApiResponse(responseCode = "200", description = "Lista de préstamos encontrada",
					content = @Content(schema = @Schema(implementation = PrestamosDTO.class))),
			@ApiResponse(responseCode = "400", description = "Estado de filtro no válido")
	})
	@GetMapping ("/prestamos")
	public ResponseEntity<ArrayList<PrestamosDTO>> listarPrestamos(
			@Parameter(description = "Nombre del socio (búsqueda parcial)")
			@RequestParam(required = false) String socio,
			@Parameter(description = "Título del videojuego (búsqueda parcial)")
			@RequestParam (required = false) String titulo,
			@Parameter(description = "Estado del préstamo: EN_PLAZO, VENCIDO o DEVUELTO", example = "EN_PLAZO")
			@RequestParam (required = false) String estado,
			@Parameter(description = "Fecha de préstamo desde (inclusive), formato ISO yyyy-MM-dd", example = "2026-01-01")
			@RequestParam (required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaDesde) throws SQLException {



		try {
			ArrayList<PrestamosDTO> prestamos = prestamosService.listarPrestamos(socio, titulo, null, estado, fechaDesde);
			prestamos.forEach(prestamo -> {
				if (!PrestamosService.ESTADO_VENCIDO.equals(prestamo.getEstado())) {
					prestamo.setDiasRetraso(0);
				}
			});
			return ResponseEntity.ok(prestamos);
		} catch (ValidacionException e) {
			return ResponseEntity.badRequest().build();
		}
	}

	@Operation(summary = "Buscar préstamo por ID",
			description = "Devuelve un préstamo concreto con su estado calculado "
					+ "(EN_PLAZO, VENCIDO o DEVUELTO) y los días de retraso.")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Préstamo encontrado",
					content = @Content(schema = @Schema(implementation = PrestamosDTO.class))),
			@ApiResponse(responseCode = "404", description = "No existe un préstamo con ese ID")
	})
	@GetMapping("/prestamos/{id}")
	public ResponseEntity<PrestamosDTO> buscarPrestamoPorId(
			@Parameter(description = "ID del préstamo", example = "1")
			@PathVariable int id) throws SQLException {
		PrestamosDTO prestamo = prestamosService.buscarPrestamoPorId(id);
		if (prestamo == null) {
			return ResponseEntity.notFound().build();
		}
		if (!PrestamosService.ESTADO_VENCIDO.equals(prestamo.getEstado())) {
			prestamo.setDiasRetraso(0);
		}
		return ResponseEntity.ok(prestamo);
	}
	@Operation(summary = "Registrar un préstamo",
			description = "Crea un nuevo préstamo para un socio y una copia. "
					+ "Aplica las reglas de negocio: socio activo, copia disponible, máximo 2 préstamos abiertos, "
					+ "edad mínima según PEGI, sin préstamos vencidos y restricciones para menores con copias deterioradas. "
					+ "La fecha límite es de 5 días para novedades (lanzamiento en los últimos 2 años) o 10 días en otro caso.")
	@ApiResponses({
			@ApiResponse(responseCode = "201", description = "Préstamo registrado correctamente",
					content = @Content(schema = @Schema(implementation = PrestamosDTO.class))),
			@ApiResponse(responseCode = "400", description = "Datos no válidos o reglas de negocio incumplidas")
	})
	@PostMapping("/prestamos")
	public ResponseEntity<PrestamosDTO> insertarPrestamo(
			@io.swagger.v3.oas.annotations.parameters.RequestBody(
					description = "Datos del préstamo: idSocio e idCopia son obligatorios",
					required = true,
					content = @Content(schema = @Schema(implementation = PrestamosDTO.class)))
			@RequestBody PrestamosDTO prestamo) throws SQLException {
		try {
			PrestamosDTO prestamoRegistrado = prestamosService.registrarPrestamo(prestamo.getIdSocio(), prestamo.getIdCopia());
			return new ResponseEntity<>(prestamoRegistrado, HttpStatus.CREATED);
		} catch (ValidacionException e) {
			return ResponseEntity.badRequest().build();
		}
	}

	@Operation(summary = "Devolver un préstamo",
			description = "Registra la devolución de un préstamo. Calcula la multa por días de retraso "
					+ "(0,50 €/día, máximo 10 €) y, si la copia se devuelve dañada, la marca como dañada "
					+ "e inactiva y añade un recargo de 5 €.")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Préstamo devuelto correctamente",
					content = @Content(schema = @Schema(implementation = PrestamosDTO.class))),
			@ApiResponse(responseCode = "400", description = "Datos de devolución no válidos"),
			@ApiResponse(responseCode = "404", description = "No existe un préstamo con ese ID"),
			@ApiResponse(responseCode = "409", description = "El préstamo ya había sido devuelto")
	})
	@PutMapping("/prestamos/{id}/devolucion")
	public ResponseEntity<PrestamosDTO> devolverPrestamo(
			@Parameter(description = "ID del préstamo a devolver", example = "1")
			@PathVariable int id,
			@Parameter(description = "Cuerpo opcional de la devolución; admite el campo 'copiaDanada' (boolean)")
			@RequestBody(required = false) JsonNode devolucion,
			@Parameter(description = "Indica si la copia se devuelve dañada (alternativa al campo copiaDanada del cuerpo)")
			@RequestParam(required = false) Boolean copiaDanada) throws SQLException {
		try {
			boolean danada = copiaDanada != null ? copiaDanada
					: devolucion != null && devolucion.path("copiaDanada").asBoolean(false);
			PrestamosDTO prestamoDevuelto = prestamosService.devolverPrestamo(id, danada);
			return ResponseEntity.ok(prestamoDevuelto);
		} catch (PrestamoNoEncontradoException e) {
			return ResponseEntity.notFound().build();
		} catch (PrestamoYaDevueltoException e) {
			return ResponseEntity.status(HttpStatus.CONFLICT).build();
		} catch (ValidacionException e) {
			return ResponseEntity.badRequest().build();
		}
	}

}
