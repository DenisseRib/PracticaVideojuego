package com.dawes.videojuegos.Repository;

import com.dawes.videojuegos.DTO.VideojuegoDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;


@Repository
public class VideojuegoRepository {
	private static final Logger logger = LoggerFactory.getLogger(VideojuegoRepository.class);
	private final DataSource dataSource;

	public VideojuegoRepository(DataSource dataSource) {
		this.dataSource = dataSource;
	}


	public ArrayList<VideojuegoDTO> buscarVideojuegos(String titulo, Integer idDesarrolladora, Integer anioDesde,
	                                                  Integer pegiMax, Boolean soloDisponibles) throws SQLException {
		StringBuilder sql = new StringBuilder(
				"SELECT id, titulo, anio_lanzamiento, pegi, id_desarrolladora, desarrolladora, "
						+ "copias_totales, copias_disponibles "
						+ "FROM v_videojuegos_detalle WHERE 1 = 1");
		ArrayList<Object> parametros = new ArrayList<>();

		if (titulo != null) {
			sql.append(" AND titulo LIKE ?");
			parametros.add("%" + titulo + "%");
		}
		if (idDesarrolladora != null) {
			sql.append(" AND id_desarrolladora = ?");
			parametros.add(idDesarrolladora);
		}
		if (anioDesde != null) {
			sql.append(" AND anio_lanzamiento >= ?");
			parametros.add(anioDesde);
		}
		if (pegiMax != null) {
			sql.append(" AND pegi <= ?");
			parametros.add(pegiMax);
		}
		if (Boolean.TRUE.equals(soloDisponibles)) {
			sql.append(" AND copias_disponibles > 0");
		}
		sql.append(" ORDER BY titulo");

		ArrayList<VideojuegoDTO> listaVideojuegos = new ArrayList<>();
		try (Connection connection = dataSource.getConnection();
		     PreparedStatement ps = connection.prepareStatement(sql.toString())) {

			for (int i = 0; i < parametros.size(); i++) {
				ps.setObject(i + 1, parametros.get(i));
			}
			logger.debug("Query a ejecutar: {}", ps);

			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					listaVideojuegos.add(mapearVideojuego(rs));
				}
			}
		}
		return listaVideojuegos;
	}

	public VideojuegoDTO buscarVideojuegoPorId(int id) throws SQLException {
		String sql = "SELECT id, titulo, anio_lanzamiento, pegi, id_desarrolladora, desarrolladora, "
				+ "copias_totales, copias_disponibles "
				+ "FROM v_videojuegos_detalle WHERE id = ?";

		try (Connection connection = dataSource.getConnection();
		     PreparedStatement ps = connection.prepareStatement(sql)) {

			ps.setInt(1, id);
			logger.debug("Query a ejecutar: {}", ps);

			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					return mapearVideojuego(rs);
				}
			}
		}
		return null;
	}

	public int insertarVideojuego(VideojuegoDTO videojuego) throws SQLException {
		String sql = "INSERT INTO videojuegos (titulo, anio_lanzamiento, pegi, id_desarrolladora) VALUES (?, ?, ?, ?)";

		try (Connection connection = dataSource.getConnection();
		     PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

			ps.setString(1, videojuego.getTitulo());
			ps.setInt(2, videojuego.getAnioLanzamiento());
			ps.setInt(3, videojuego.getPegi());
			ps.setInt(4, videojuego.getIdDesarrolladora());
			logger.debug("Query a ejecutar: {}", ps);
			ps.executeUpdate();

			try (ResultSet claves = ps.getGeneratedKeys()) {
				if (claves.next()) {
					return claves.getInt(1);
				}
			}
		}
		throw new SQLException("La base de datos no ha devuelto el id del videojuego insertado");
	}

	private VideojuegoDTO mapearVideojuego(ResultSet rs) throws SQLException {
		return new VideojuegoDTO(
				rs.getInt("id"),
				rs.getString("titulo"),
				rs.getInt("anio_lanzamiento"),
				rs.getInt("pegi"),
				rs.getInt("id_desarrolladora"),
				rs.getString("desarrolladora"),
				rs.getInt("copias_totales"),
				rs.getInt("copias_disponibles")
		);
	}
}
