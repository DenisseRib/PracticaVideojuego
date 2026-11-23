package com.dawes.videojuegos.controlador.rest;

import com.dawes.videojuegos.DTO.PrestamosDTO;
import com.dawes.videojuegos.DTO.VideojuegoDTO;
import com.dawes.videojuegos.service.PrestamoNoEncontradoException;
import com.dawes.videojuegos.service.PrestamoYaDevueltoException;
import com.dawes.videojuegos.service.PrestamosService;
import com.dawes.videojuegos.service.ValidacionException;
import com.fasterxml.jackson.databind.JsonNode;
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
	public ResponseEntity<ArrayList<PrestamosDTO>> listarPrestamos(@RequestParam(required = false) String socio,
	                                                               @RequestParam (required = false) String titulo,
	                                                               @RequestParam (required = false) String estado,
	                                                               @RequestParam (required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaDesde) throws SQLException {



		try {
			ArrayList<PrestamosDTO> prestamos = prestamosService.listarPrestamos(socio, titulo, null, estado, fechaDesde);
			prestamos.forEach(prestamo -> {
				if (!PrestamosService.ESTADO_VENCIDO.equals(prestamo.getEstado())) {
					prestamo.setDiasRetraso(0);
				}
			});
			return ResponseEntity.ok(prestamos);
		} catch (ValidacionException e) {
			return ResponseEntity.badRequest().build();
		}
	}

	@GetMapping("/prestamos/{id}")
	public ResponseEntity<PrestamosDTO> buscarPrestamoPorId(@PathVariable int id) throws SQLException {
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
	public ResponseEntity<PrestamosDTO> insertarPrestamo(@RequestBody PrestamosDTO prestamo) throws SQLException {
		try {
			PrestamosDTO prestamoRegistrado = prestamosService.registrarPrestamo(prestamo.getIdSocio(), prestamo.getIdCopia());
			return new ResponseEntity<>(prestamoRegistrado, HttpStatus.CREATED);
		} catch (ValidacionException e) {
			return ResponseEntity.badRequest().build();
		}
	}

	@PutMapping("/prestamos/{id}/devolucion")
	public ResponseEntity<PrestamosDTO> devolverPrestamo(@PathVariable int id,
	                                          @RequestBody(required = false) JsonNode devolucion,
	                                          @RequestParam(required = false) Boolean copiaDanada) throws SQLException {
		try {
			boolean danada = copiaDanada != null ? copiaDanada
					: devolucion != null && devolucion.path("copiaDanada").asBoolean(false);
			PrestamosDTO prestamoDevuelto = prestamosService.devolverPrestamo(id, danada);
			return ResponseEntity.ok(prestamoDevuelto);
		} catch (PrestamoNoEncontradoException e) {
			return ResponseEntity.notFound().build();
		} catch (PrestamoYaDevueltoException e) {
			return ResponseEntity.status(HttpStatus.CONFLICT).build();
		} catch (ValidacionException e) {
			return ResponseEntity.badRequest().build();
		}
	}

}
