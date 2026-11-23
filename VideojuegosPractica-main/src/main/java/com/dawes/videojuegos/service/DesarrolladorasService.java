package com.dawes.videojuegos.service;

import com.dawes.videojuegos.DTO.DesarrolladoraDTO;
import com.dawes.videojuegos.interfaz.IDesarrolladoraDAO;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.ArrayList;

@Service
public class DesarrolladorasService {
	private final IDesarrolladoraDAO desarrolladoraDAO;

	public DesarrolladorasService(IDesarrolladoraDAO desarrolladoraDAO) {
		this.desarrolladoraDAO = desarrolladoraDAO;
	}

	public ArrayList<DesarrolladoraDTO> listarDesarrolladoras() throws SQLException {
		return desarrolladoraDAO.listarDesarrolladoras();
	}
}
