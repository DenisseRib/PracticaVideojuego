package com.dawes.videojuegos.controlador.rest;

import com.dawes.videojuegos.DTO.PrestamosDTO;
import com.dawes.videojuegos.DTO.VideojuegoDTO;
import com.dawes.videojuegos.service.PrestamosService;
import com.dawes.videojuegos.service.ValidacionException;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;

@RestController
@RequestMapping("/api")
public class PrestamosRestController {
	private final PrestamosService prestamosService;
	public PrestamosRestController(PrestamosService prestamosService) {
		this.prestamosService = prestamosService;
	}
	@GetMapping ("/prestamos")
	public ResponseEntity<?> listarPrestamos(@RequestParam(required = false) String socio,
	                                                               @RequestParam (required = false) String titulo,
	                                                               @RequestParam (required = false) String estado,
	                                                               @RequestParam (required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaDesde) throws SQLException {


		 /*texto contenido en el nombre o apellidos del socio.
				titulo : texto contenido en el título del juego.
				estado : EN_PLAZO , VENCIDO o DEVUELTO . Si no se envía, todos. Si se envía otro
		valor → 400 .
				fechaDesde : préstamos con fecha_prestamo mayor o igual (formato YYYY-MM-
				DD*/
		try {
			ArrayList<PrestamosDTO> prestamos = prestamosService.listarPrestamos(socio, titulo, null, estado, fechaDesde);
			prestamos.forEach(prestamo -> {
				if (!PrestamosService.ESTADO_VENCIDO.equals(prestamo.getEstado())) {
					prestamo.setDiasRetraso(0);
				}
			});
			return ResponseEntity.ok(prestamos);
		} catch (ValidacionException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	@GetMapping("/prestamos/{id}")
	public ResponseEntity<?> buscarPrestamoPorId(@PathVariable int id) throws SQLException {
		PrestamosDTO prestamo = prestamosService.buscarPrestamoPorId(id);
		if (prestamo == null) {
			return ResponseEntity.notFound().build();
		}
		if (!PrestamosService.ESTADO_VENCIDO.equals(prestamo.getEstado())) {
			prestamo.setDiasRetraso(0);
		}
		return ResponseEntity.ok(prestamo);
	}
	@PostMapping("/prestamos")
	public ResponseEntity<?> insertarPrestamo(@RequestBody PrestamosDTO prestamo) throws SQLException {
		try {
			PrestamosDTO prestamoRegistrado = prestamosService.registrarPrestamo(prestamo.getIdSocio(), prestamo.getIdCopia());
			return new ResponseEntity<>(prestamoRegistrado, HttpStatus.CREATED);
		} catch (ValidacionException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

}
