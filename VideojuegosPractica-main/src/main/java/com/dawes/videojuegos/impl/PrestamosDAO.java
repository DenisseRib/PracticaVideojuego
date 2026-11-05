package com.dawes.videojuegos.impl;


import com.dawes.videojuegos.DTO.PrestamosDTO;

import com.dawes.videojuegos.Repository.PrestamosRepository;

import com.dawes.videojuegos.interfaz.IPrestamosDAO;
import org.springframework.stereotype.Repository;

import java.sql.SQLException;
import java.util.ArrayList;

@Repository
public class PrestamosDAO implements IPrestamosDAO {
	private final PrestamosRepository prestamosRepository;

	public PrestamosDAO(PrestamosRepository prestamosRepository) {
		this.prestamosRepository = prestamosRepository;
	}

	@Override
	public ArrayList<PrestamosDTO> listarPrestamos(String estado) throws SQLException {
		return prestamosRepository.listarPrestamos(estado);
	}
}