package com.dawes.videojuegos.Repository;

import com.dawes.videojuegos.DTO.ContadoresInicioDTO;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

@Repository
public class InicioRepository {
	private final DataSource dataSource;

	public InicioRepository(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	/**
	 * Cuenta videojuegos, socios activos, préstamos abiertos (fecha_devolucion IS NULL)
	 * y préstamos vencidos (abiertos con fecha_limite anterior a la fecha indicada).
	 */
	public ContadoresInicioDTO obtenerContadores(LocalDate hoy) throws SQLException {
		String sql = "SELECT "
				+ "(SELECT COUNT(*) FROM videojuegos) AS videojuegos, "
				+ "(SELECT COUNT(*) FROM socios WHERE activo = 1) AS socios_activos, "
				+ "(SELECT COUNT(*) FROM prestamos WHERE fecha_devolucion IS NULL) AS prestamos_abiertos, "
				+ "(SELECT COUNT(*) FROM prestamos WHERE fecha_devolucion IS NULL AND fecha_limite < ?) AS prestamos_vencidos";

		try (Connection connection = dataSource.getConnection();
		     PreparedStatement ps = connection.prepareStatement(sql)) {

			ps.setDate(1, Date.valueOf(hoy));
			try (ResultSet rs = ps.executeQuery()) {
				rs.next();
				return new ContadoresInicioDTO(
						rs.getInt("videojuegos"),
						rs.getInt("socios_activos"),
						rs.getInt("prestamos_abiertos"),
						rs.getInt("prestamos_vencidos")
				);
			}
		}
	}
}
