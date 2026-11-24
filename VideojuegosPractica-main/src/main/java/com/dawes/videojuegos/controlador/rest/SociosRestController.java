package com.dawes.videojuegos.controlador.rest;

import com.dawes.videojuegos.DTO.SociosDTO;
import com.dawes.videojuegos.service.SociosService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.ArrayList;

@RestController
@RequestMapping("/api")
@Tag(name = "Socios", description = "Gestión de socios de la biblioteca")
public class SociosRestController {
	private final SociosService sociosService;

	public SociosRestController(SociosService sociosService) {
		this.sociosService = sociosService;
	}

	@Operation(summary = "Listar socios",
			description = "Devuelve los socios filtrados por nombre/apellidos, email y estado activo. "
					+ "Todos los filtros son opcionales y combinables.")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Lista de socios (puede estar vacía)",
					content = @Content(schema = @Schema(implementation = SociosDTO.class)))
	})
	@GetMapping("/socios")
	public ResponseEntity<ArrayList<SociosDTO>> listarSocios(
			@Parameter(description = "Texto contenido en el nombre o los apellidos")
			@RequestParam(required = false) String nombre,
			@Parameter(description = "Texto contenido en el email")
			@RequestParam(required = false) String email,
			@Parameter(description = "Filtrar por socios activos o inactivos")
			@RequestParam(required = false) Boolean activo) throws SQLException {
		return ResponseEntity.ok(sociosService.listarSocios(nombre, email, activo));
	}

	@Operation(summary = "Obtener socio por ID",
			description = "Devuelve un socio con su edad y número de préstamos abiertos calculados.")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Socio encontrado",
					content = @Content(schema = @Schema(implementation = SociosDTO.class))),
			@ApiResponse(responseCode = "404", description = "No existe un socio con ese ID")
	})
	@GetMapping("/socios/{id}")
	public ResponseEntity<SociosDTO> obtenerSocio(
			@Parameter(description = "ID del socio", example = "2")
			@PathVariable int id) throws SQLException {
		return ResponseEntity.ok(sociosService.buscarSocioPorId(id));
	}

	@Operation(summary = "Crear socio",
			description = "Crea un socio activo con fecha de alta de hoy. Valida datos obligatorios, "
					+ "formato de email y email único.")
	@ApiResponses({
			@ApiResponse(responseCode = "201", description = "Socio creado",
					content = @Content(schema = @Schema(implementation = SociosDTO.class))),
			@ApiResponse(responseCode = "400", description = "Datos del socio no válidos"),
			@ApiResponse(responseCode = "409", description = "El email ya está registrado")
	})
	@PostMapping("/socios")
	public ResponseEntity<SociosDTO> crearSocio(
			@io.swagger.v3.oas.annotations.parameters.RequestBody(
					description = "Datos del socio: nombre, apellidos, email y fechaNacimiento son obligatorios",
					required = true,
					content = @Content(schema = @Schema(implementation = SociosDTO.class)))
			@RequestBody SociosDTO socio) throws SQLException {
		SociosDTO socioCreado = sociosService.insertarSocio(socio);
		return ResponseEntity.status(HttpStatus.CREATED).body(socioCreado);
	}

	@Operation(summary = "Actualizar socio",
			description = "Actualiza los datos de un socio. El email puede conservarse, pero no puede "
					+ "repetirse en otro socio.")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Socio actualizado",
					content = @Content(schema = @Schema(implementation = SociosDTO.class))),
			@ApiResponse(responseCode = "400", description = "Datos del socio no válidos"),
			@ApiResponse(responseCode = "404", description = "No existe un socio con ese ID"),
			@ApiResponse(responseCode = "409", description = "El email ya pertenece a otro socio")
	})
	@PutMapping("/socios/{id}")
	public ResponseEntity<SociosDTO> actualizarSocio(
			@Parameter(description = "ID del socio", example = "2")
			@PathVariable int id,
			@io.swagger.v3.oas.annotations.parameters.RequestBody(
					description = "Datos del socio a actualizar, incluido el campo activo",
					required = true,
					content = @Content(schema = @Schema(implementation = SociosDTO.class)))
			@RequestBody SociosDTO socio) throws SQLException {
		return ResponseEntity.ok(sociosService.actualizarSocio(id, socio));
	}

	@Operation(summary = "Borrar socio",
			description = "Elimina un socio que no tenga ningún préstamo registrado.")
	@ApiResponses({
			@ApiResponse(responseCode = "204", description = "Socio eliminado"),
			@ApiResponse(responseCode = "404", description = "No existe un socio con ese ID"),
			@ApiResponse(responseCode = "409", description = "El socio tiene préstamos registrados")
	})
	@DeleteMapping("/socios/{id}")
	public ResponseEntity<Void> eliminarSocio(
			@Parameter(description = "ID del socio", example = "2")
			@PathVariable int id) throws SQLException {
		sociosService.eliminarSocio(id);
		return ResponseEntity.noContent().build();
	}
}