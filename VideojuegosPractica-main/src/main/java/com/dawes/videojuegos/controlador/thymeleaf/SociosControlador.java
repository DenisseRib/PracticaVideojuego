package com.dawes.videojuegos.controlador.thymeleaf;

import com.dawes.videojuegos.DTO.SociosDTO;
import com.dawes.videojuegos.service.SociosService;
import com.dawes.videojuegos.service.ValidacionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;

@Controller
@RequestMapping("/socios")
public class SociosControlador {
	private static final Logger logger = LoggerFactory.getLogger(SociosControlador.class);
	private static final String MENSAJE_ERROR_BBDD = "Se ha producido un error con la base de datos. Inténtalo de nuevo más tarde.";

	private final SociosService sociosService;

	public SociosControlador(SociosService sociosService) {
		this.sociosService = sociosService;
	}

	@GetMapping("/listado")
	public String listado(@RequestParam(required = false) String nombreOApellidos,
	                      @RequestParam(required = false) String email,
	                      @RequestParam(required = false) Boolean activo,
	                      Model model) {

		model.addAttribute("nombreOApellidos", nombreOApellidos);
		model.addAttribute("email", email);
		model.addAttribute("activo", activo);

		try {
			model.addAttribute("listaSocios", sociosService.listarSocios(nombreOApellidos, email, activo));
		} catch (SQLException e) {
			logger.error("Error al listar los socios", e);
			model.addAttribute("listaSocios", new ArrayList<SociosDTO>());
			model.addAttribute("error", MENSAJE_ERROR_BBDD);
		}
		return "socios/listado";
	}

	@GetMapping("/insertar")
	public String formularioInsertar() {
		return "socios/insertar";
	}

	@PostMapping("/insertar")
	public String insertar(@RequestParam(required = false) String nombre,
	                       @RequestParam(required = false) String apellidos,
	                       @RequestParam(required = false) String email,
	                       @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaNacimiento,
	                       Model model, RedirectAttributes redirectAttributes) {
		try {
			SociosDTO socio = new SociosDTO(nombre, apellidos, email, fechaNacimiento);
			int idNuevo = sociosService.insertarSocio(socio);

			redirectAttributes.addFlashAttribute("mensajeOk",
					"Socio '" + socio.getNombre() + " " + socio.getApellidos() + "' insertado correctamente (id " + idNuevo + ")");
			return "redirect:/socios/insertar";

		} catch (SQLException e) {
			logger.error("Error al insertar el socio", e);
			model.addAttribute("error", MENSAJE_ERROR_BBDD);
		} catch (ValidacionException e) {
			model.addAttribute("error", e.getMessage());
		}

		model.addAttribute("nombre", nombre);
		model.addAttribute("apellidos", apellidos);
		model.addAttribute("email", email);
		model.addAttribute("fechaNacimiento", fechaNacimiento);
		return "socios/insertar";
	}
}