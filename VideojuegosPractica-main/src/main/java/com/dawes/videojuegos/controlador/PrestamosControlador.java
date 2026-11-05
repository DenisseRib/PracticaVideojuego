package com.dawes.videojuegos.controlador;


import com.dawes.videojuegos.DTO.PrestamosDTO;

import com.dawes.videojuegos.service.PrestamosService;
import com.dawes.videojuegos.service.ValidacionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.sql.SQLException;
import java.util.ArrayList;

@Controller
@RequestMapping("/prestamos")
public class PrestamosControlador {
	private static final Logger logger = LoggerFactory.getLogger(PrestamosControlador.class);
	private static final String MENSAJE_ERROR_BBDD = "Se ha producido un error con la base de datos. Inténtalo de nuevo más tarde.";

	private final PrestamosService prestamosService;

	public PrestamosControlador(PrestamosService prestamosService) {
		this.prestamosService = prestamosService;
	}

	@GetMapping("/listado")
	public String listado(@RequestParam(required = false) String estado, Model model) {
		model.addAttribute("estado", estado);
		try {
			model.addAttribute("listaPrestamos", prestamosService.listarPrestamos(estado));
		} catch (ValidacionException e) {
			model.addAttribute("listaPrestamos", new ArrayList<PrestamosDTO>());
			model.addAttribute("error", e.getMessage());
		} catch (SQLException e) {
			logger.error("Error al listar los préstamos", e);
			model.addAttribute("listaPrestamos", new ArrayList<PrestamosDTO>());
			model.addAttribute("error", MENSAJE_ERROR_BBDD);
		}
		return "prestamos/listado";
	}
}