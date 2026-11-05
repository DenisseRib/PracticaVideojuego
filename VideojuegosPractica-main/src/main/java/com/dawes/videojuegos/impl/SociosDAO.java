package com.dawes.videojuegos.impl;

import com.dawes.videojuegos.DTO.SociosDTO;
import com.dawes.videojuegos.Repository.SociosRepository;
import com.dawes.videojuegos.interfaz.ISociosDAO;
import org.springframework.stereotype.Repository;

import java.sql.SQLException;
import java.util.ArrayList;

@Repository
public class SociosDAO implements ISociosDAO {
	private final SociosRepository sociosRepository;

	public SociosDAO(SociosRepository sociosRepository) {
		this.sociosRepository = sociosRepository;
	}

	@Override
	public ArrayList<SociosDTO> listarSocios(String nombre, String apellidos, String email, Boolean activo) throws SQLException {
		return sociosRepository.listarSocios(nombre, apellidos, email, activo);
	}

	@Override
	public boolean existeEmail(String email) throws SQLException {
		return sociosRepository.existeEmail(email);
	}

	@Override
	public int insertarSocio(SociosDTO socio) throws SQLException {
		return sociosRepository.insertarSocio(socio);
	}
}