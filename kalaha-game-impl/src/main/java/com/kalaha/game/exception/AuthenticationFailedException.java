package com.kalaha.game.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class AuthenticationFailedException extends KalahaException {

	private static final String VAL_007 = "VAL_007";

	public AuthenticationFailedException(String message) {
		super(message, VAL_007);
	}

}
