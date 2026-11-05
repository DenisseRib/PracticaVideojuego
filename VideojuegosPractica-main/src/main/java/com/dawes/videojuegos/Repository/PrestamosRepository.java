package com.dawes.videojuegos.Repository;

import com.dawes.videojuegos.DTO.PrestamosDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;

@Repository
public class PrestamosRepository {
	private static final Logger logger = LoggerFactory.getLogger(PrestamosRepository.class );
	private final DataSource dataSource;

	public PrestamosRepository(DataSource dataSource) {
		this.dataSource = dataSource;
	}
	public ArrayList<PrestamosDTO> listarPrestamos(String estado) throws SQLException {
		StringBuilder sql = new StringBuilder(
				"SELECT p.id, CONCAT(s.nombre, ' ', s.apellidos) AS socio, "
						+ "v.titulo AS videojuego, pl.nombre AS plataforma, c.codigo_inventario AS codigoCopia, "
						+ "p.fecha_prestamo, p.fecha_limite, p.fecha_devolucion, p.multa, "
						+ "CASE WHEN p.fecha_devolucion IS NOT NULL THEN 'Devuelto' "
						+ "     WHEN p.fecha_limite < CURDATE() THEN 'Vencido' "
						+ "     ELSE 'Abierto' END AS estado "
						+ "FROM prestamos p "
						+ "JOIN socios s ON s.id = p.id_socio "
						+ "JOIN copias c ON c.id = p.id_copia "
						+ "JOIN videojuegos v ON v.id = c.id_videojuego "
						+ "JOIN plataformas pl ON pl.id = c.id_plataforma "
						+ "WHERE 1 = 1");


		if ("ABIERTOS".equals(estado)) {
			sql.append(" AND p.fecha_devolucion IS NULL");
		} else if ("VENCIDOS".equals(estado)) {
			sql.append(" AND p.fecha_devolucion IS NULL AND p.fecha_limite < CURDATE()");
		} else if ("DEVUELTOS".equals(estado)) {
			sql.append(" AND p.fecha_devolucion IS NOT NULL");
		}
		sql.append(" ORDER BY p.fecha_prestamo DESC, p.id DESC");

		ArrayList<PrestamosDTO> lista = new ArrayList<>();
		try (Connection connection = dataSource.getConnection();
		     PreparedStatement ps = connection.prepareStatement(sql.toString())) {
			logger.debug("Query a ejecutar: {}", ps);
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					lista.add(mapearPrestamo(rs));
				}
			}
		}
		return lista;
	}

	private PrestamosDTO mapearPrestamo(ResultSet rs) throws SQLException {
		Date fechaDevolucion = rs.getDate("fecha_devolucion"); // puede ser null
		return new PrestamosDTO(
				rs.getInt("id"),
				rs.getString("socio"),
				rs.getString("videojuego"),
				rs.getString("plataforma"),
				rs.getString("codigoCopia"),
				rs.getDate("fecha_prestamo").toLocalDate(),
				rs.getDate("fecha_limite").toLocalDate(),
				fechaDevolucion == null ? null : fechaDevolucion.toLocalDate(),
				rs.getBigDecimal("multa"),
				rs.getString("estado")
		);
	}
}
