package com.dawes.videojuegos.DTO;

public class CopiaDTO {
	private Integer id;
	private String codigoInventario;
	private String titulo;
	private Integer anioLanzamiento;
	private Integer pegi;
	private String plataforma;
	private boolean activa;
	private int prestamosAbiertos;

	public CopiaDTO(Integer id, String codigoInventario, String titulo, Integer anioLanzamiento, Integer pegi,
	                String plataforma, boolean activa, int prestamosAbiertos) {
		this.id = id;
		this.codigoInventario = codigoInventario;
		this.titulo = titulo;
		this.anioLanzamiento = anioLanzamiento;
		this.pegi = pegi;
		this.plataforma = plataforma;
		this.activa = activa;
		this.prestamosAbiertos = prestamosAbiertos;
	}

	public Integer getId() { return id; }
	public String getCodigoInventario() { return codigoInventario; }
	public String getTitulo() { return titulo; }
	public Integer getAnioLanzamiento() { return anioLanzamiento; }
	public Integer getPegi() { return pegi; }
	public String getPlataforma() { return plataforma; }
	public boolean isActiva() { return activa; }
	public int getPrestamosAbiertos() { return prestamosAbiertos; }
}
