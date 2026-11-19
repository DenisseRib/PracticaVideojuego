package com.dawes.videojuegos.service;

import com.dawes.videojuegos.DTO.SociosDTO;
import com.dawes.videojuegos.DTO.VideojuegoDTO;
import com.dawes.videojuegos.interfaz.ISociosDAO;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.regex.Pattern;

@Service
public class SociosService {
	private static final int LONGITUD_MAXIMA_NOMBRE = 40;
	private static final int LONGITUD_MAXIMA_APELLIDOS = 80;
	private static final int LONGITUD_MAXIMA_EMAIL = 100;
	private static final Pattern PATRON_EMAIL = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

	private final ISociosDAO sociosDAO;

	public SociosService(ISociosDAO sociosDAO) {
		this.sociosDAO = sociosDAO;
	}

	public ArrayList<SociosDTO> listarSocios(String nombreOApellidos, String email, Boolean activo) throws SQLException {
		return sociosDAO.listarSocios(limpiar(nombreOApellidos), limpiar(email), activo);
	}

	public SociosDTO buscarSocioPorId(int id) throws SQLException {
		return sociosDAO.buscarSocioPorId(id);
	}
	public int insertarSocio(SociosDTO socio) throws SQLException, ValidacionException {
		ArrayList<String> errores = new ArrayList<>();

		String nombre = socio.getNombre() == null ? "" : socio.getNombre().trim();
		if (nombre.isEmpty()) {
			errores.add("El nombre es obligatorio.");
		} else if (nombre.length() > LONGITUD_MAXIMA_NOMBRE) {
			errores.add("El nombre no puede tener más de " + LONGITUD_MAXIMA_NOMBRE + " caracteres.");
		}

		String apellidos = socio.getApellidos() == null ? "" : socio.getApellidos().trim();
		if (apellidos.isEmpty()) {
			errores.add("Los apellidos son obligatorios.");
		} else if (apellidos.length() > LONGITUD_MAXIMA_APELLIDOS) {
			errores.add("Los apellidos no pueden tener más de " + LONGITUD_MAXIMA_APELLIDOS + " caracteres.");
		}

		String email = socio.getEmail() == null ? "" : socio.getEmail().trim();
		if (email.isEmpty()) {
			errores.add("El email es obligatorio.");
		} else if (email.length() > LONGITUD_MAXIMA_EMAIL || !PATRON_EMAIL.matcher(email).matches()) {
			errores.add("El email no tiene un formato válido.");
		} else if (sociosDAO.existeEmail(email)) {
			errores.add("Ya existe un socio con el email " + email);
		}

		LocalDate fechaNacimiento = socio.getFechaNacimiento();
		if (fechaNacimiento == null) {
			errores.add("La fecha de nacimiento es obligatoria.");
		} else if (fechaNacimiento.isAfter(LocalDate.now())) {
			errores.add("La fecha de nacimiento no puede ser futura.");
		}

		if (!errores.isEmpty()) {
			throw new ValidacionException(String.join(" ", errores));
		}

		socio.setNombre(nombre);
		socio.setApellidos(apellidos);
		socio.setEmail(email);
		socio.setFechaAlta(LocalDate.now());
		socio.setActivo(true);
		return sociosDAO.insertarSocio(socio);
	}
	public String limpiar(String texto) {
		return (texto == null || texto.isBlank()) ? null : texto.trim();
	}
}