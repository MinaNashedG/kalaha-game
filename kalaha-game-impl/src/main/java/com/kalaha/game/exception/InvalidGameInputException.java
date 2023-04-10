package com.kalaha.game.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidGameInputException extends RuntimeException {

	private static final String VAL_001 = "VAL_001";

	public InvalidGameInputException(String message) {
		super(message);
	}

	public String getCode() {
		return VAL_001;
	}
}
