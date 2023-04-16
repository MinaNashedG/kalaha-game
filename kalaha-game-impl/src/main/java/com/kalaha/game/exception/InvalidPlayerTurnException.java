package com.kalaha.game.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidPlayerTurnException extends KalahaException {

	private static final String VAL_003 = "VAL_003";

	public InvalidPlayerTurnException(String message) {
		super(message, VAL_003);
	}

}
