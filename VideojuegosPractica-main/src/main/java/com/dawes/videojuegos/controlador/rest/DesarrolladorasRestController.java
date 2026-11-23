package com.dawes.videojuegos.controlador.rest;

import com.dawes.videojuegos.DTO.DesarrolladoraDTO;
import com.dawes.videojuegos.service.DesarrolladorasService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLException;
import java.util.ArrayList;

@RestController
@RequestMapping("/api")
public class DesarrolladorasRestController {
	private final DesarrolladorasService desarrolladorasService;

	public DesarrolladorasRestController(DesarrolladorasService desarrolladorasService) {
		this.desarrolladorasService = desarrolladorasService;
	}

	@GetMapping("/desarrolladoras")
	public ResponseEntity<ArrayList<DesarrolladoraDTO>> listarDesarrolladoras() throws SQLException {
		ArrayList<DesarrolladoraDTO> desarrolladoras = desarrolladorasService.listarDesarrolladoras();
		return new ResponseEntity<>(desarrolladoras, HttpStatus.OK);
	}
}
