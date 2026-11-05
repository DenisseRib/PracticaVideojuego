package com.dawes.videojuegos.interfaz;

import com.dawes.videojuegos.DTO.SociosDTO;

import java.sql.SQLException;
import java.util.ArrayList;

public interface ISociosDAO {

	ArrayList<SociosDTO> listarSocios(String nombre, String apellidos, String email, Boolean activo) throws SQLException;

	boolean existeEmail(String email) throws SQLException;

	int insertarSocio(SociosDTO socio) throws SQLException;
}