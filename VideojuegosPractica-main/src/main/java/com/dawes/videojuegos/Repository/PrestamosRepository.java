package com.dawes.videojuegos.Repository;

import com.dawes.videojuegos.DTO.PrestamosDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;

@Repository
public class PrestamosRepository {
	private static final Logger logger = LoggerFactory.getLogger(PrestamosRepository.class );
	private static final String SQL_SELECT_PRESTAMOS =
			"SELECT p.id, p.id_copia, CONCAT(s.nombre, ' ', s.apellidos) AS socio, "
					+ "v.titulo AS videojuego, pl.nombre AS plataforma, c.codigo_inventario AS codigoCopia, "
					+ "p.fecha_prestamo, p.fecha_limite, p.fecha_devolucion, p.multa "
					+ "FROM prestamos p "
					+ "JOIN socios s ON s.id = p.id_socio "
					+ "JOIN copias c ON c.id = p.id_copia "
					+ "JOIN videojuegos v ON v.id = c.id_videojuego "
					+ "JOIN plataformas pl ON pl.id = c.id_plataforma ";

	private final DataSource dataSource;

	public PrestamosRepository(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public ArrayList<PrestamosDTO> listarPrestamos(String socio, String videojuego, String estado,
	                                               LocalDate fechaDesde, LocalDate hoy) throws SQLException {
		StringBuilder sql = new StringBuilder(SQL_SELECT_PRESTAMOS + "WHERE 1 = 1");
		ArrayList<Object> parametros = new ArrayList<>();

		if (socio != null) {
			sql.append(" AND CONCAT(s.nombre, ' ', s.apellidos) LIKE ?");
			parametros.add("%" + socio + "%");
		}
		if (videojuego != null) {
			sql.append(" AND v.titulo LIKE ?");
			parametros.add("%" + videojuego + "%");
		}
		if (fechaDesde != null) {
			sql.append(" AND p.fecha_prestamo >= ?");
			parametros.add(Date.valueOf(fechaDesde));
		}
		if ("ABIERTOS".equals(estado)) {
			sql.append(" AND p.fecha_devolucion IS NULL");
		} else if ("EN_PLAZO".equals(estado)) {
			sql.append(" AND p.fecha_devolucion IS NULL AND p.fecha_limite >= ?");
			parametros.add(Date.valueOf(hoy));
		} else if ("VENCIDO".equals(estado)) {
			sql.append(" AND p.fecha_devolucion IS NULL AND p.fecha_limite < ?");
			parametros.add(Date.valueOf(hoy));
		} else if ("DEVUELTO".equals(estado)) {
			sql.append(" AND p.fecha_devolucion IS NOT NULL");
		}
		sql.append(" ORDER BY p.fecha_prestamo DESC, p.id DESC");

		ArrayList<PrestamosDTO> lista = new ArrayList<>();
		try (Connection connection = dataSource.getConnection();
		     PreparedStatement ps = connection.prepareStatement(sql.toString())) {
			for (int i = 0; i < parametros.size(); i++) {
				ps.setObject(i + 1, parametros.get(i));
			}
			logger.debug("Query a ejecutar: {}", ps);
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					lista.add(mapearPrestamo(rs));
				}
			}
		}
		return lista;
	}

	public PrestamosDTO buscarPrestamo(int idPrestamo) throws SQLException {
		String sql = SQL_SELECT_PRESTAMOS + "WHERE p.id = ?";
		try (Connection connection = dataSource.getConnection();
		     PreparedStatement ps = connection.prepareStatement(sql)) {
			ps.setInt(1, idPrestamo);
			logger.debug("Query a ejecutar: {}", ps);
			try (ResultSet rs = ps.executeQuery()) {
				return rs.next() ? mapearPrestamo(rs) : null;
			}
		}
	}

	public int contarPrestamosVencidosSocio(int idSocio, LocalDate hoy) throws SQLException {
		String sql = "SELECT COUNT(*) FROM prestamos WHERE id_socio = ? AND fecha_devolucion IS NULL AND fecha_limite < ?";
		try (Connection connection = dataSource.getConnection();
		     PreparedStatement ps = connection.prepareStatement(sql)) {
			ps.setInt(1, idSocio);
			ps.setDate(2, Date.valueOf(hoy));
			logger.debug("Query a ejecutar: {}", ps);
			try (ResultSet rs = ps.executeQuery()) {
				rs.next();
				return rs.getInt(1);
			}
		}
	}

	public int insertarPrestamo(int idSocio, int idCopia, LocalDate fechaPrestamo, LocalDate fechaLimite) throws SQLException {
		String sql = "INSERT INTO prestamos (id_socio, id_copia, fecha_prestamo, fecha_limite, fecha_devolucion, multa) "
				+ "VALUES (?, ?, ?, ?, NULL, 0)";
		try (Connection connection = dataSource.getConnection();
		     PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
			ps.setInt(1, idSocio);
			ps.setInt(2, idCopia);
			ps.setDate(3, Date.valueOf(fechaPrestamo));
			ps.setDate(4, Date.valueOf(fechaLimite));
			logger.debug("Query a ejecutar: {}", ps);
			ps.executeUpdate();

			try (ResultSet claves = ps.getGeneratedKeys()) {
				if (claves.next()) {
					return claves.getInt(1);
				}
			}
		}
		throw new SQLException("La base de datos no ha devuelto el id del préstamo insertado");
	}


	public void devolverPrestamo(int idPrestamo, int idCopia, LocalDate fechaDevolucion, BigDecimal multa,
	                             boolean copiaDanada) throws SQLException {
		String sqlPrestamo = "UPDATE prestamos SET fecha_devolucion = ?, multa = ? "
				+ "WHERE id = ? AND fecha_devolucion IS NULL";
		String sqlCopia = "UPDATE copias SET estado = 'DAÑADO', activa = 0 WHERE id = ?";

		try (Connection connection = dataSource.getConnection()) {
			connection.setAutoCommit(false);
			try {
				try (PreparedStatement psPrestamo = connection.prepareStatement(sqlPrestamo)) {
					psPrestamo.setDate(1, Date.valueOf(fechaDevolucion));
					psPrestamo.setBigDecimal(2, multa);
					psPrestamo.setInt(3, idPrestamo);
					logger.debug("Query a ejecutar: {}", psPrestamo);
					if (psPrestamo.executeUpdate() != 1) {
						throw new SQLException("El préstamo " + idPrestamo + " no se ha podido actualizar (¿ya estaba devuelto?)");
					}
				}
				if (copiaDanada) {
					try (PreparedStatement psCopia = connection.prepareStatement(sqlCopia)) {
						psCopia.setInt(1, idCopia);
						logger.debug("Query a ejecutar: {}", psCopia);
						psCopia.executeUpdate();
					}
				}
				connection.commit();
			} catch (SQLException e) {
				connection.rollback();
				throw e;
			} finally {
				connection.setAutoCommit(true);
			}
		}
	}

	private PrestamosDTO mapearPrestamo(ResultSet rs) throws SQLException {
		Date fechaDevolucion = rs.getDate("fecha_devolucion"); // puede ser null
		return new PrestamosDTO(
				rs.getInt("id"),
				rs.getInt("id_copia"),
				rs.getString("socio"),
				rs.getString("videojuego"),
				rs.getString("plataforma"),
				rs.getString("codigoCopia"),
				rs.getDate("fecha_prestamo").toLocalDate(),
				rs.getDate("fecha_limite").toLocalDate(),
				fechaDevolucion == null ? null : fechaDevolucion.toLocalDate(),
				rs.getBigDecimal("multa")
		);
	}
}
