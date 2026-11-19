package com.dawes.videojuegos.controlador.rest;

import com.dawes.videojuegos.DTO.SociosDTO;
import com.dawes.videojuegos.DTO.VideojuegoDTO;
import com.dawes.videojuegos.service.SociosService;
import com.dawes.videojuegos.service.ValidacionException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.ArrayDeque;
import java.util.ArrayList;

@RestController
@RequestMapping("/api")
public class SociosRestController {
	private final SociosService sociosService;

	public SociosRestController(SociosService sociosService) {
		this.sociosService = sociosService;
	}

	@GetMapping("/socios")
	public ResponseEntity<ArrayList<SociosDTO>> buscarSocio(
			@RequestParam(required = false) String nombre,
			@RequestParam(required = false) String email,
			@RequestParam(required = false) Boolean activo)
			throws SQLException {
		ArrayList<SociosDTO> socios = sociosService.listarSocios(nombre, email, activo);
		return new ResponseEntity<>(socios, HttpStatus.OK);
	}
	@GetMapping("/socios/{id}")
	public ResponseEntity<SociosDTO> buscarSocioPorId(@PathVariable int id) throws SQLException {
		SociosDTO socio = sociosService.buscarSocioPorId(id);
		if (socio != null) {
			return new ResponseEntity<>(socio, HttpStatus.OK);
		}
		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	}
	@PostMapping("/socios")
	public ResponseEntity<SociosDTO> insertarSocio(@RequestBody SociosDTO socio) throws SQLException, ValidacionException {
		int resultado = sociosService.insertarSocio(socio);

		if (resultado != 0) {
			return new ResponseEntity<>(socio, HttpStatus.CREATED);
		}
		return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
	}
}