package com.dawes.videojuegos.service;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class ValidacionException extends ResponseStatusException {
	private static final long serialVersionUID = 1L;

	public ValidacionException(String message) {
		this(HttpStatus.BAD_REQUEST, message);
	}

	public ValidacionException(HttpStatus status, String message) {
		super(status, message);
	}

	@Override
	public String getMessage() {
		return getReason();
	}
}
