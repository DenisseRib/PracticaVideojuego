package com.dawes.videojuegos.DTO;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDate;

@Schema(description = "Datos de un préstamo de videojuego")
public class PrestamosDTO {
	@Schema(description = "ID del préstamo", example = "1")
	private Integer id;
	@Schema(description = "ID del socio que realiza el préstamo", example = "3")
	private Integer idSocio;
	@Schema(description = "ID de la copia prestada", example = "12")
	private Integer idCopia;
	@Schema(description = "Nombre completo del socio", example = "Ana García")
	private String socio;
	@Schema(description = "Título del videojuego", example = "The Legend of Zelda")
	private String videojuego;
	@Schema(description = "Plataforma de la copia", example = "Nintendo Switch")
	private String plataforma;
	@Schema(description = "Código de inventario de la copia", example = "SW-0001")
	private String codigoCopia;
	@Schema(description = "Fecha del préstamo (yyyy-MM-dd)", example = "2026-11-20")
	private LocalDate fechaPrestamo;
	@Schema(description = "Fecha límite de devolución (yyyy-MM-dd)", example = "2026-11-30")
	private LocalDate fechaLimite;
	@Schema(description = "Fecha de devolución real; null si sigue prestado", example = "2026-11-28")
	private LocalDate fechaDevolucion;
	@Schema(description = "Multa acumulada en euros", example = "1.50")
	private BigDecimal multa;
	@Schema(description = "Estado calculado: EN_PLAZO, VENCIDO o DEVUELTO", example = "EN_PLAZO")
	private String estado;
	@Schema(description = "Días de retraso respecto a la fecha límite", example = "0")
	private int diasRetraso;
	@Schema(description = "Número de renovaciones realizadas (máximo 1)", example = "0")
	private Integer renovaciones;

	public PrestamosDTO() {
	}

	public PrestamosDTO(Integer id, Integer idCopia, String socio, String videojuego, String plataforma,
	                    String codigoCopia, LocalDate fechaPrestamo, LocalDate fechaLimite,
	                    LocalDate fechaDevolucion, BigDecimal multa, Integer renovaciones) {
		this.id = id;
		this.idCopia = idCopia;
		this.socio = socio;
		this.videojuego = videojuego;
		this.plataforma = plataforma;
		this.codigoCopia = codigoCopia;
		this.fechaPrestamo = fechaPrestamo;
		this.fechaLimite = fechaLimite;
		this.fechaDevolucion = fechaDevolucion;
		this.multa = multa;
		this.renovaciones = renovaciones;
	}





	public Integer getId() { return id; }
	public Integer getIdSocio() { return idSocio; }
	public void setIdSocio(Integer idSocio) { this.idSocio = idSocio; }
	public Integer getIdCopia() { return idCopia; }
	public String getSocio() { return socio; }
	public String getVideojuego() { return videojuego; }
	public String getPlataforma() { return plataforma; }
	public String getCodigoCopia() { return codigoCopia; }
	public LocalDate getFechaPrestamo() { return fechaPrestamo; }
	public LocalDate getFechaLimite() { return fechaLimite; }
	public LocalDate getFechaDevolucion() { return fechaDevolucion; }
	public BigDecimal getMulta() { return multa; }
	public Integer getRenovaciones() { return renovaciones; }
	public String getEstado() { return estado; }
	public void setEstado(String estado) { this.estado = estado; }
	public int getDiasRetraso() { return diasRetraso; }
	public void setDiasRetraso(int diasRetraso) { this.diasRetraso = diasRetraso; }

}
