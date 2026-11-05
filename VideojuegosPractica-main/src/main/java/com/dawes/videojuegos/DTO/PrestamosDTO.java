package com.dawes.videojuegos.DTO;

import java.math.BigDecimal;
import java.time.LocalDate;


	public class PrestamosDTO {
		private Integer id;
		private String socio;
		private String videojuego;
		private String plataforma;
		private String codigoCopia;
		private LocalDate fechaPrestamo;
		private LocalDate fechaLimite;
		private LocalDate fechaDevolucion; // puede ser null: todavía no se ha devuelto
		private BigDecimal multa;
		private String estado;             // Devuelto, Abierto o Vencido

		public PrestamosDTO() {
		}

		public PrestamosDTO(Integer id, String socio, String videojuego, String plataforma, String codigoCopia,
		                   LocalDate fechaPrestamo, LocalDate fechaLimite, LocalDate fechaDevolucion,
		                   BigDecimal multa, String estado) {
			this.id = id;
			this.socio = socio;
			this.videojuego = videojuego;
			this.plataforma = plataforma;
			this.codigoCopia = codigoCopia;
			this.fechaPrestamo = fechaPrestamo;
			this.fechaLimite = fechaLimite;
			this.fechaDevolucion = fechaDevolucion;
			this.multa = multa;
			this.estado = estado;
		}

		public Integer getId() {
			return id; }
		public String getSocio() {
			return socio; }
		public String getVideojuego() {
			return videojuego; }
		public String getPlataforma() {
			return plataforma; }
		public String getCodigoCopia() {
			return codigoCopia; }
		public LocalDate getFechaPrestamo() {
			return fechaPrestamo; }
		public LocalDate getFechaLimite() {
			return fechaLimite; }
		public LocalDate getFechaDevolucion() {
			return fechaDevolucion; }
		public BigDecimal getMulta() {
			return multa; }
		public String getEstado() {
			return estado; }
	}


