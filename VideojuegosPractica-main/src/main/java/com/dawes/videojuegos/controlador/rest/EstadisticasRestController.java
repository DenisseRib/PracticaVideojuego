package com.dawes.videojuegos.controlador.rest;

import com.dawes.videojuegos.DTO.ContadoresInicioDTO;
import com.dawes.videojuegos.service.InicioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLException;

@RestController
@RequestMapping("/api")
public class EstadisticasRestController {
	private final InicioService inicioService;

	public EstadisticasRestController(InicioService inicioService) {
		this.inicioService = inicioService;
	}

	@GetMapping("/estadisticas")
	public ResponseEntity<ContadoresInicioDTO> obtenerEstadisticas() throws SQLException {
		ContadoresInicioDTO contadores = inicioService.obtenerContadores();
		return new ResponseEntity<>(contadores, HttpStatus.OK);
	}
}
