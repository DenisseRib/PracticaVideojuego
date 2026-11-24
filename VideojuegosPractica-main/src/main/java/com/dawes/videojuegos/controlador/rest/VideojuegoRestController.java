package com.dawes.videojuegos.controlador.rest;

import com.dawes.videojuegos.DTO.VideojuegoDTO;
import com.dawes.videojuegos.service.VideojuegoService;
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
@Tag(name = "Videojuegos", description = "Catálogo de videojuegos y copias disponibles")
public class VideojuegoRestController {

	private final VideojuegoService videojuegoService;

	public VideojuegoRestController(VideojuegoService videojuegoService) {
		this.videojuegoService = videojuegoService;
	}

	@Operation(summary = "Listar videojuegos",
			description = "Lista o busca videojuegos con filtros opcionales y combinables. "
					+ "Incluye el número de copias totales y disponibles de cada juego.")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Lista de videojuegos (puede estar vacía)",
					content = @Content(schema = @Schema(implementation = VideojuegoDTO.class)))
	})
	@GetMapping("/videojuegos")
	public ResponseEntity<ArrayList<VideojuegoDTO>> buscarVideojuegos(
			@Parameter(description = "Texto contenido en el título")
			@RequestParam(required = false) String titulo,
			@Parameter(description = "ID de la desarrolladora", example = "1")
			@RequestParam(required = false) Integer idDesarrolladora,
			@Parameter(description = "Año de lanzamiento mínimo", example = "2010")
			@RequestParam(required = false) Integer anioDesde,
			@Parameter(description = "PEGI máximo", example = "12")
			@RequestParam(required = false) Integer pegiMax,
			@Parameter(description = "Si es true, solo juegos con al menos una copia disponible")
			@RequestParam(required = false) Boolean soloDisponibles) throws SQLException {
		return ResponseEntity.ok(videojuegoService.buscarVideojuegos(
				titulo, idDesarrolladora, anioDesde, pegiMax, soloDisponibles));
	}

	@Operation(summary = "Obtener videojuego por ID",
			description = "Devuelve un videojuego con sus contadores de copias.")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Videojuego encontrado",
					content = @Content(schema = @Schema(implementation = VideojuegoDTO.class))),
			@ApiResponse(responseCode = "404", description = "No existe un videojuego con ese ID")
	})
	@GetMapping("/videojuegos/{id}")
	public ResponseEntity<VideojuegoDTO> obtenerVideojuegoPorId(
			@Parameter(description = "ID del videojuego", example = "3")
			@PathVariable int id) throws SQLException {
		return ResponseEntity.ok(videojuegoService.buscarVideojuegoPorId(id));
	}

	@Operation(summary = "Crear videojuego",
			description = "Crea un videojuego validando título, año de lanzamiento, PEGI y desarrolladora existente.")
	@ApiResponses({
			@ApiResponse(responseCode = "201", description = "Videojuego creado",
					content = @Content(schema = @Schema(implementation = VideojuegoDTO.class))),
			@ApiResponse(responseCode = "400", description = "Datos del videojuego no válidos"),
			@ApiResponse(responseCode = "404", description = "La desarrolladora no existe")
	})
	@PostMapping("/videojuegos")
	public ResponseEntity<VideojuegoDTO> insertarVideojuego(
			@io.swagger.v3.oas.annotations.parameters.RequestBody(
					description = "Datos del videojuego: titulo, anioLanzamiento, pegi e idDesarrolladora",
					required = true,
					content = @Content(schema = @Schema(implementation = VideojuegoDTO.class)))
			@RequestBody VideojuegoDTO videojuego) throws SQLException {
		VideojuegoDTO videojuegoCreado = videojuegoService.insertarVideojuego(videojuego);
		return ResponseEntity.status(HttpStatus.CREATED).body(videojuegoCreado);
	}
}

