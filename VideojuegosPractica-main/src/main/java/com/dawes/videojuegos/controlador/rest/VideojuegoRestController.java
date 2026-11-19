package com.dawes.videojuegos.controlador.rest;

import com.dawes.videojuegos.DTO.VideojuegoDTO;
import com.dawes.videojuegos.service.ValidacionException;
import com.dawes.videojuegos.service.VideojuegoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.ArrayList;

@RestController
@RequestMapping("/api")
public class VideojuegoRestController {

	private final VideojuegoService videojuegoService;

	public VideojuegoRestController(VideojuegoService videojuegoService) {
		this.videojuegoService = videojuegoService;
	}

	@PostMapping("/videojuegos")
	public ResponseEntity<VideojuegoDTO> insertarVideojuego(@RequestBody VideojuegoDTO
			                                                        videojuego) throws SQLException, ValidacionException {
		int resultado = videojuegoService.insertarVideojuego(videojuego);

		if (resultado != 0) {
			return new ResponseEntity<>(videojuego, HttpStatus.CREATED);
		}
		return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@GetMapping("/videojuegos/{id}")
	public ResponseEntity<VideojuegoDTO> obtenerVideojuegoPorId(@PathVariable int id) throws SQLException {
		VideojuegoDTO videojuego = videojuegoService.buscarVideojuegoPorId(id);
		if (videojuego != null) {
			return new ResponseEntity<>(videojuego, HttpStatus.OK);
		}
		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	}
	@GetMapping("/videojuegos")
	public ResponseEntity<ArrayList<VideojuegoDTO>> buscarVideojuegos(
			@RequestParam(required = false) String titulo,
			@RequestParam(required = false) Integer idDesarrolladora,
			@RequestParam(required = false) Integer anioDesde,
			@RequestParam(required = false) Integer pegiMax,
			@RequestParam(required = false ) Boolean soloDisponibles) throws SQLException {
		ArrayList<VideojuegoDTO> videojuegos = videojuegoService.buscarVideojuegos(
				titulo, idDesarrolladora, anioDesde, pegiMax, soloDisponibles);
		return new ResponseEntity<>(videojuegos, HttpStatus.OK);
	}
}
