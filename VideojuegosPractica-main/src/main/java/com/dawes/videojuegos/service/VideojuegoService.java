package com.dawes.videojuegos.service;

import com.dawes.videojuegos.DTO.DesplegableDTO;
import com.dawes.videojuegos.DTO.VideojuegoDTO;
import com.dawes.videojuegos.interfaz.IDesarrolladoraDAO;
import com.dawes.videojuegos.interfaz.IVideojuegoDAO;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


@Service
public class VideojuegoService {
	public static final int ANIO_MINIMO = 1970;
	private static final int LONGITUD_MAXIMA_TITULO = 100;
	private static final List<Integer> PEGI_VALIDOS = List.of(3, 7, 12, 16, 18);

	private final IVideojuegoDAO videojuegoDAO;
	private final IDesarrolladoraDAO desarrolladoraDAO;

	public VideojuegoService(IVideojuegoDAO videojuegoDAO, IDesarrolladoraDAO desarrolladoraDAO) {
		this.videojuegoDAO = videojuegoDAO;
		this.desarrolladoraDAO = desarrolladoraDAO;
	}

	public ArrayList<VideojuegoDTO> buscarVideojuegos(String titulo, Integer idDesarrolladora, Integer anioDesde,
	                                                  Integer pegiMax, Boolean soloDisponibles) throws SQLException {

		String tituloLimpio = (titulo == null || titulo.isBlank()) ? null : titulo.trim();
		return videojuegoDAO.buscarVideojuegos(tituloLimpio, idDesarrolladora, anioDesde, pegiMax, soloDisponibles);
	}

	public VideojuegoDTO buscarVideojuegoPorId(int id) throws SQLException {
		return videojuegoDAO.buscarVideojuegoPorId(id);
	}

	public ArrayList<DesplegableDTO> listarDesarrolladoras() throws SQLException {
		return desarrolladoraDAO.listarDesplegable();
	}

	public List<Integer> listarPegiValidos() {
		return PEGI_VALIDOS;
	}


	public int insertarVideojuego(VideojuegoDTO videojuego) throws SQLException, ValidacionException {
		ArrayList<String> errores = new ArrayList<>();
		int anioActual = LocalDate.now().getYear();

		String titulo = videojuego.getTitulo() == null ? "" : videojuego.getTitulo().trim();
		if (titulo.isEmpty()) {
			errores.add("El título es obligatorio.");
		} else if (titulo.length() > LONGITUD_MAXIMA_TITULO) {
			errores.add("El título no puede tener más de " + LONGITUD_MAXIMA_TITULO + " caracteres.");
		}

		Integer anioLanzamiento = videojuego.getAnioLanzamiento();
		if (anioLanzamiento == null) {
			errores.add("El año de lanzamiento es obligatorio.");
		} else if (anioLanzamiento < ANIO_MINIMO || anioLanzamiento > anioActual) {
			errores.add("El año de lanzamiento debe estar entre " + ANIO_MINIMO + " y " + anioActual + ".");
		}

		if (videojuego.getPegi() == null || !PEGI_VALIDOS.contains(videojuego.getPegi())) {
			errores.add("El PEGI debe ser uno de estos valores: 3, 7, 12, 16 o 18.");
		}

		if (videojuego.getIdDesarrolladora() == null) {
			errores.add("Debes seleccionar una desarrolladora.");
		} else if (!desarrolladoraDAO.existeDesarrolladora(videojuego.getIdDesarrolladora())) {
			errores.add("La desarrolladora seleccionada no existe.");
		}

		if (!errores.isEmpty()) {
			throw new ValidacionException(String.join(" ", errores));
		}

		videojuego.setTitulo(titulo);
		return videojuegoDAO.insertarVideojuego(videojuego);
	}
}
