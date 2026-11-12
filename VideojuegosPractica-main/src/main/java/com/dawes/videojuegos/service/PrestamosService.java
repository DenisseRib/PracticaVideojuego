package com.dawes.videojuegos.service;

import com.dawes.videojuegos.DTO.CopiaDTO;
import com.dawes.videojuegos.DTO.DesplegableDTO;
import com.dawes.videojuegos.DTO.PrestamosDTO;
import com.dawes.videojuegos.DTO.SociosDTO;
import com.dawes.videojuegos.interfaz.ICopiasDAO;
import com.dawes.videojuegos.interfaz.IPrestamosDAO;
import com.dawes.videojuegos.interfaz.ISociosDAO;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
public class PrestamosService {
	public static final String ESTADO_EN_PLAZO = "EN_PLAZO";
	public static final String ESTADO_VENCIDO = "VENCIDO";
	public static final String ESTADO_DEVUELTO = "DEVUELTO";
	private static final String FILTRO_ABIERTOS = "ABIERTOS";
	private static final List<String> ESTADOS_VALIDOS = List.of(ESTADO_EN_PLAZO, ESTADO_VENCIDO, ESTADO_DEVUELTO);

	private static final int MAXIMO_PRESTAMOS_ABIERTOS = 3;
	private static final int DIAS_PRESTAMO_NOVEDAD = 7;
	private static final int DIAS_PRESTAMO_NORMAL = 15;
	private static final BigDecimal MULTA_POR_DIA = new BigDecimal("0.50");

	private final IPrestamosDAO prestamosDAO;
	private final ISociosDAO sociosDAO;
	private final ICopiasDAO copiasDAO;

	public PrestamosService(IPrestamosDAO prestamosDAO, ISociosDAO sociosDAO, ICopiasDAO copiasDAO) {
		this.prestamosDAO = prestamosDAO;
		this.sociosDAO = sociosDAO;
		this.copiasDAO = copiasDAO;
	}


	public ArrayList<PrestamosDTO> listarPrestamos( String socio, String videojuego, String estado,
	                                               LocalDate fechaDesde) throws SQLException, ValidacionException {
		String estadoLimpio = limpiar(estado);
		if (estadoLimpio != null) {
			estadoLimpio = estadoLimpio.toUpperCase();
			if (!ESTADOS_VALIDOS.contains(estadoLimpio)) {
				throw new ValidacionException("El estado debe ser: en plazo, vencido o devuelto.");
			}
		}
		LocalDate hoy = LocalDate.now();
		ArrayList<PrestamosDTO> listaPrestamos =
				prestamosDAO.listarPrestamos(limpiar(socio), limpiar(videojuego), estadoLimpio, fechaDesde, hoy);
		listaPrestamos.forEach(prestamo -> calcularEstado(prestamo, hoy));
		return listaPrestamos;
	}

	public ArrayList<PrestamosDTO> listarPrestamosAbiertos(String socio) throws SQLException {
		LocalDate hoy = LocalDate.now();
		ArrayList<PrestamosDTO> listaPrestamos =
				prestamosDAO.listarPrestamos(limpiar(socio), null, FILTRO_ABIERTOS, null, hoy);
		listaPrestamos.forEach(prestamo -> calcularEstado(prestamo, hoy));
		return listaPrestamos;
	}

	public ArrayList<DesplegableDTO> listarSociosActivos() throws SQLException {
		return sociosDAO.listarActivosDesplegable();
	}

	public ArrayList<DesplegableDTO> listarCopiasDisponibles() throws SQLException {
		return copiasDAO.listarDisponiblesDesplegable();
	}


	public PrestamosDTO registrarPrestamo(Integer idSocio, Integer idCopia) throws SQLException, ValidacionException {
		if (idSocio == null) {
			throw new ValidacionException("Debes seleccionar un socio.");
		}
		if (idCopia == null) {
			throw new ValidacionException("Debes seleccionar una copia.");
		}

		SociosDTO socio = sociosDAO.buscarSocio(idSocio);
		if (socio == null) {
			throw new ValidacionException("El socio seleccionado no existe.");
		}
		CopiaDTO copia = copiasDAO.buscarCopia(idCopia);
		if (copia == null) {
			throw new ValidacionException("La copia seleccionada no existe.");
		}

		LocalDate hoy = LocalDate.now();
		ArrayList<String> reglasIncumplidas = new ArrayList<>();

		if (!socio.isActivo()) {
			reglasIncumplidas.add("RN-1: El socio no está activo.");
		}
		if (!copia.isActiva() || copia.getPrestamosAbiertos() > 0) {
			reglasIncumplidas.add("RN-2: La copia " + copia.getCodigoInventario() + " no está disponible (inactiva o ya prestada).");
		}
		if (socio.getPrestamosAbiertos() >= MAXIMO_PRESTAMOS_ABIERTOS) {
			reglasIncumplidas.add("RN-3: El socio ya tiene " + MAXIMO_PRESTAMOS_ABIERTOS + " préstamos abiertos.");
		}
		if (socio.getEdad() < copia.getPegi()) {
			reglasIncumplidas.add("RN-4: El socio tiene " + socio.getEdad() + " años y el juego es PEGI " + copia.getPegi() + ".");
		}
		if (prestamosDAO.contarPrestamosVencidosSocio(idSocio, hoy) > 0) {
			reglasIncumplidas.add("RN-5: El socio tiene préstamos vencidos sin devolver.");
		}

		if (!reglasIncumplidas.isEmpty()) {
			throw new ValidacionException(String.join(" ", reglasIncumplidas));
		}

		LocalDate fechaLimite = hoy.plusDays(esNovedad(copia.getAnioLanzamiento(), hoy) ? DIAS_PRESTAMO_NOVEDAD : DIAS_PRESTAMO_NORMAL);
		int idNuevo = prestamosDAO.insertarPrestamo(idSocio, idCopia, hoy, fechaLimite);

		PrestamosDTO prestamoCreado = prestamosDAO.buscarPrestamo(idNuevo);
		calcularEstado(prestamoCreado, hoy);
		return prestamoCreado;
	}

	private boolean esNovedad(int anioLanzamiento, LocalDate hoy) {
		return anioLanzamiento >= hoy.getYear() - 1;
	}


	public PrestamosDTO devolverPrestamo(Integer idPrestamo, boolean copiaDanada) throws SQLException, ValidacionException {
		if (idPrestamo == null) {
			throw new ValidacionException("Debes indicar el préstamo a devolver.");
		}
		PrestamosDTO prestamo = prestamosDAO.buscarPrestamo(idPrestamo);
		if (prestamo == null) {
			throw new ValidacionException("El préstamo seleccionado no existe.");
		}
		if (prestamo.getFechaDevolucion() != null) {
			throw new ValidacionException("El préstamo " + idPrestamo + " ya fue devuelto el " + prestamo.getFechaDevolucion() + ".");
		}

		LocalDate hoy = LocalDate.now();
		BigDecimal multa = MULTA_POR_DIA.multiply(BigDecimal.valueOf(diasDeRetraso(prestamo.getFechaLimite(), hoy)));

		prestamosDAO.devolverPrestamo(idPrestamo, prestamo.getIdCopia(), hoy, multa, copiaDanada);

		PrestamosDTO prestamoDevuelto = prestamosDAO.buscarPrestamo(idPrestamo);
		calcularEstado(prestamoDevuelto, hoy);
		return prestamoDevuelto;
	}


	private long diasDeRetraso(LocalDate fechaLimite, LocalDate fechaReferencia) {
		return Math.max(0, ChronoUnit.DAYS.between(fechaLimite, fechaReferencia));
	}

	private void calcularEstado(PrestamosDTO prestamo, LocalDate hoy) {
		if (prestamo.getFechaDevolucion() != null) {
			prestamo.setEstado(ESTADO_DEVUELTO);
			prestamo.setDiasRetraso((int) diasDeRetraso(prestamo.getFechaLimite(), prestamo.getFechaDevolucion()));
		} else if (prestamo.getFechaLimite().isBefore(hoy)) {
			prestamo.setEstado(ESTADO_VENCIDO);
			prestamo.setDiasRetraso((int) diasDeRetraso(prestamo.getFechaLimite(), hoy));
		} else {
			prestamo.setEstado(ESTADO_EN_PLAZO);
			prestamo.setDiasRetraso(0);
		}
	}

	private String limpiar(String texto) {
		return (texto == null || texto.isBlank()) ? null : texto.trim();
	}
}
