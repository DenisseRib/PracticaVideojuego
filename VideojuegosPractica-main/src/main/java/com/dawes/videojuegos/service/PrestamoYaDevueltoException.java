package com.dawes.videojuegos.service;

import org.springframework.http.HttpStatus;

public class PrestamoYaDevueltoException extends ValidacionException {
	private static final long serialVersionUID = 1L;

	public PrestamoYaDevueltoException(String message) {
		super(HttpStatus.CONFLICT, message);
	}
}
