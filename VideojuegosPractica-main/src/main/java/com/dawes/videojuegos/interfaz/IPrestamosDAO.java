package com.dawes.videojuegos.interfaz;



import com.dawes.videojuegos.DTO.PrestamosDTO;

import java.sql.SQLException;
import java.util.ArrayList;

public interface IPrestamosDAO {
	ArrayList<PrestamosDTO> listarPrestamos(String estado) throws SQLException;
}
