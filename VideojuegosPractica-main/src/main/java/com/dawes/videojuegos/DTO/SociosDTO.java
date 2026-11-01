package com.dawes.videojuegos.DTO;

import java.time.LocalDate;

public class SociosDTO {
    private int idSocio;
    private String nombre;
    private String apellidos;
    private String email;
    private Integer edad;
    private Integer prestamosAbiertos;


    private boolean activo;

   

    public SociosDTO(int idSocio, String nombre, String apellidos, String email, Integer edad, Integer prestamosAbiertos, boolean activo) {
        this.idSocio = idSocio;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.email = email;
        this.edad = edad;
        this.prestamosAbiertos = prestamosAbiertos;

        this.activo = activo;
    }


    public SociosDTO(int idSocio, String nombre, String apellidos, String email, boolean activo, int prestamosAbiertos) {
        this.idSocio = idSocio;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.email = email;
        this.activo = activo;
        this.prestamosAbiertos = prestamosAbiertos;
    }

    public int getIdSocio() {
        return idSocio;
    }

    public void setIdSocio(int idSocio) {
        this.idSocio = idSocio;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getEdad() {
        return edad;
    }

    public void setEdad(Integer edad) {
        this.edad = edad;
    }

    public Integer getPrestamosAbiertos() {
        return prestamosAbiertos;
    }

    public void setPrestamosAbiertos(Integer prestamosAbiertos) {
        this.prestamosAbiertos = prestamosAbiertos;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

}
