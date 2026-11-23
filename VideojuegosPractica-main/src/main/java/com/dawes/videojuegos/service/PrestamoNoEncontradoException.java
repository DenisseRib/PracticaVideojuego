package com.dawes.videojuegos.service;

public class PrestamoNoEncontradoException extends ValidacionException {
	private static final long serialVersionUID = 1L;

	public PrestamoNoEncontradoException(String message) {
		super(message);
	}
}
