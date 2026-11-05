package com.dawes.videojuegos.service;

import com.dawes.videojuegos.DTO.PrestamosDTO;
import com.dawes.videojuegos.interfaz.IPrestamosDAO;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Service
public class PrestamosService {
	private static final List<String> ESTADOS_VALIDOS = List.of("ABIERTOS", "VENCIDOS", "DEVUELTOS");

	private final IPrestamosDAO prestamosDAO;

	public PrestamosService(IPrestamosDAO prestamoDAO) {
		this.prestamosDAO = prestamoDAO;
	}

	public ArrayList<PrestamosDTO> listarPrestamos(String estado) throws SQLException, ValidacionException {
		String estadoLimpio = (estado == null || estado.isBlank()) ? null : estado.trim().toUpperCase();

		if (estadoLimpio != null && !ESTADOS_VALIDOS.contains(estadoLimpio)) {
			throw new ValidacionException("El estado debe ser: abiertos, vencidos o devueltos.");
		}
		return prestamosDAO.listarPrestamos(estadoLimpio);
	}
}