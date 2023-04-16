package com.kalaha.game.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class PlayerAlreadyExistsException extends KalahaException {

	private static final String VAL_005 = "VAL_005";

	public PlayerAlreadyExistsException(String message) {
		super(message, VAL_005);
	}

}
