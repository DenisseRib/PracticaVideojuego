package com.dawes.videojuegos.service;

import org.springframework.http.HttpStatus;

public class PrestamoNoEncontradoException extends ValidacionException {
	private static final long serialVersionUID = 1L;

	public PrestamoNoEncontradoException(String message) {
		super(HttpStatus.NOT_FOUND, message);
	}
}
