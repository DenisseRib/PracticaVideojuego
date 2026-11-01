package com.dawes.videojuegos.DTO;

/** Pareja id + texto para rellenar los desplegables (select) desde la base de datos. */
public class DesplegableDTO {
	private Integer id;
	private String descripcion;

	public DesplegableDTO(Integer id, String descripcion) {
		this.id = id;
		this.descripcion = descripcion;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
}
