package com.dawes.videojuegos.impl;

import com.dawes.videojuegos.DTO.DesplegableDTO;
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
	public ArrayList<SociosDTO> listarSocios(String nombreOApellidos, String email, Boolean activo) throws SQLException {
		return sociosRepository.listarSocios(nombreOApellidos, email, activo);
	}

	@Override
	public SociosDTO buscarSocio(int idSocio) throws SQLException {
		return sociosRepository.buscarSocio(idSocio);
	}

	@Override
	public ArrayList<DesplegableDTO> listarActivosDesplegable() throws SQLException {
		return sociosRepository.listarActivosDesplegable();
	}

	@Override
	public boolean existeEmail(String email) throws SQLException {
		return sociosRepository.existeEmail(email);
	}

	@Override
	public boolean existeEmailDeOtroSocio(String email, int idSocio) throws SQLException {
		return sociosRepository.existeEmailDeOtroSocio(email, idSocio);
	}

	@Override
	public int insertarSocio(SociosDTO socio) throws SQLException {
		return sociosRepository.insertarSocio(socio);
	}

	@Override
	public int actualizarSocio(SociosDTO socio) throws SQLException {
		return sociosRepository.actualizarSocio(socio);
	}

	@Override
	public int eliminarSocio(int id) throws SQLException {
		return sociosRepository.eliminarSocio(id);
	}

	@Override
	public int contarPrestamosSocio(int id) throws SQLException {
		return sociosRepository.contarPrestamosSocio(id);
	}
	@Override
	public SociosDTO  buscarSocioPorId(int id) throws SQLException {
		return sociosRepository.buscarSocioPorId(id);
	}
}