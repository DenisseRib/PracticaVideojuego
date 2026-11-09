package com.dawes.videojuegos.Repository;

import com.dawes.videojuegos.DTO.CopiaDTO;
import com.dawes.videojuegos.DTO.DesplegableDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

@Repository
public class CopiasRepository {
	private static final Logger logger = LoggerFactory.getLogger(CopiasRepository.class);
	private final DataSource dataSource;

	public CopiasRepository(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public ArrayList<DesplegableDTO> listarDisponiblesDesplegable() throws SQLException {
		String sql = "SELECT c.id, CONCAT(v.titulo, ' (', pl.nombre, ') - ', c.codigo_inventario) AS descripcion "
				+ "FROM copias c "
				+ "JOIN videojuegos v ON v.id = c.id_videojuego "
				+ "JOIN plataformas pl ON pl.id = c.id_plataforma "
				+ "WHERE c.activa = 1 "
				+ "AND NOT EXISTS (SELECT 1 FROM prestamos p WHERE p.id_copia = c.id AND p.fecha_devolucion IS NULL) "
				+ "ORDER BY v.titulo, pl.nombre, c.codigo_inventario";
		ArrayList<DesplegableDTO> listaCopias = new ArrayList<>();

		try (Connection connection = dataSource.getConnection();
		     PreparedStatement sentencia = connection.prepareStatement(sql);
		     ResultSet resultado = sentencia.executeQuery()) {

			while (resultado.next()) {
				listaCopias.add(new DesplegableDTO(resultado.getInt("id"), resultado.getString("descripcion")));
			}
		}
		return listaCopias;
	}

	public CopiaDTO buscarCopia(int idCopia) throws SQLException {
		String sql = "SELECT c.id, c.codigo_inventario, v.titulo, v.anio_lanzamiento, v.pegi, "
				+ "pl.nombre AS plataforma, c.activa, "
				+ "(SELECT COUNT(*) FROM prestamos p WHERE p.id_copia = c.id AND p.fecha_devolucion IS NULL) AS prestamos_abiertos "
				+ "FROM copias c "
				+ "JOIN videojuegos v ON v.id = c.id_videojuego "
				+ "JOIN plataformas pl ON pl.id = c.id_plataforma "
				+ "WHERE c.id = ?";

		try (Connection connection = dataSource.getConnection();
		     PreparedStatement sentencia = connection.prepareStatement(sql)) {
			sentencia.setInt(1, idCopia);
			logger.debug("Query a ejecutar: {}", sentencia);

			try (ResultSet resultado = sentencia.executeQuery()) {
				if (!resultado.next()) {
					return null;
				}
				return new CopiaDTO(
						resultado.getInt("id"),
						resultado.getString("codigo_inventario"),
						resultado.getString("titulo"),
						resultado.getInt("anio_lanzamiento"),
						resultado.getInt("pegi"),
						resultado.getString("plataforma"),
						resultado.getBoolean("activa"),
						resultado.getInt("prestamos_abiertos"));
			}
		}
	}
}
