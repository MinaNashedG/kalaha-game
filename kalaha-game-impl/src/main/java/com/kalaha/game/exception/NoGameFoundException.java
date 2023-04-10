package com.kalaha.game.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class NoGameFoundException extends RuntimeException {

	private static final String VAL_004 = "VAL_004";

	public NoGameFoundException(String message) {
		super(message);
	}

	public String getCode() {
		return VAL_004;
	}
}
