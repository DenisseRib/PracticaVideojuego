package com.dawes.videojuegos.DTO;

public class VideojuegoMasPrestadoDTO {
	private String titulo;
	private int totalPrestamos;

	public VideojuegoMasPrestadoDTO() {
	}

	public VideojuegoMasPrestadoDTO(String titulo, int totalPrestamos) {
		this.titulo = titulo;
		this.totalPrestamos = totalPrestamos;
	}

	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public int getTotalPrestamos() {
		return totalPrestamos;
	}

	public void setTotalPrestamos(int totalPrestamos) {
		this.totalPrestamos = totalPrestamos;
	}
}
