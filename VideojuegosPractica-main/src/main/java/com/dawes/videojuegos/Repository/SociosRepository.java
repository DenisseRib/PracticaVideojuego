package com.dawes.videojuegos.Repository;

import com.dawes.videojuegos.DTO.SociosDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;

@Repository
public class SociosRepository {

    private static final Logger logger = LoggerFactory.getLogger(SociosRepository.class);

    private final DataSource dataSource;

    public SociosRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Integer obtenerEdadSocio(Integer idSocio) {
        String sql = "SELECT fecha_nacimiento FROM socios WHERE id_socio = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idSocio);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    LocalDate fechaNacimiento = rs.getObject("fecha_nacimiento", LocalDate.class);
                    return calcularEdad(fechaNacimiento);
                }
            }
        } catch (SQLException e) {
            logger.error("Error al obtener la edad del socio con id {}", idSocio, e);
        }

        return null;
    }

    public int contarPrestamosAbiertos(Integer idSocio) {
        String sql = "SELECT COUNT(*) FROM prestamos "
                + "WHERE id_socio = ? AND fecha_devolucion IS NULL";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idSocio);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        } catch (SQLException e) {
            logger.error("Error al contar los préstamos abiertos del socio {}", idSocio, e);
            throw new RuntimeException("Error al contar los préstamos abiertos del socio " + idSocio, e);
        }
    }

    public ArrayList<SociosDTO> listarSocios() {
        String sql = "SELECT s.id_socio, s.nombre, s.apellidos, s.email, s.activo, s.fecha_nacimiento, "
                + "       COUNT(p.id_prestamo) AS prestamos_abiertos "
                + "FROM socios s "
                + "LEFT JOIN prestamos p "
                + "       ON p.id_socio = s.id_socio AND p.fecha_devolucion IS NULL "
                + "GROUP BY s.id_socio, s.nombre, s.apellidos, s.email, s.activo, s.fecha_nacimiento "
                + "ORDER BY s.nombre";

        ArrayList<SociosDTO> socios = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                socios.add(new SociosDTO(
                        rs.getInt("id_socio"),
                        rs.getString("nombre"),
                        rs.getString("apellidos"),
                        rs.getString("email"),
                        rs.getBoolean("activo"),
                        rs.getInt("prestamos_abiertos")
                ));
            }
            return socios;

        } catch (SQLException e) {
            logger.error("Error al listar los socios", e);
            throw new RuntimeException("Error al listar los socios", e);
        }
    }

    private Integer calcularEdad(LocalDate fechaNacimiento) {
        if (fechaNacimiento == null) {
            return null;
        }
        return Period.between(fechaNacimiento, LocalDate.now()).getYears();
    }}
