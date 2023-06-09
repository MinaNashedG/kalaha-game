package com.kalaha.game.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidGameStateException extends KalahaException {

	private static final String VAL_002 = "VAL_002";

	public InvalidGameStateException(String message) {
		super(message, VAL_002);
	}

}
