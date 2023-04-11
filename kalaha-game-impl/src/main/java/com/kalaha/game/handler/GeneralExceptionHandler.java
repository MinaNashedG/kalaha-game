package com.kalaha.game.handler;

import com.kalaha.game.exception.InvalidGameInputException;
import com.kalaha.game.exception.InvalidGameStateException;
import com.kalaha.game.exception.InvalidPlayerTurnException;
import com.kalaha.game.exception.KalahaException;
import com.kalaha.game.exception.NoGameFoundException;
import com.kalaha.game.model.KalahaErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GeneralExceptionHandler {

	@ExceptionHandler({InvalidGameInputException.class, InvalidPlayerTurnException.class,
			InvalidGameStateException.class})
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public KalahaErrorResponse handle(KalahaException ex) {

		return KalahaErrorResponse.builder()
				.code(ex.getCode())
				.message(ex.getMessage())
				.build();
	}

	@ExceptionHandler(NoGameFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public KalahaErrorResponse handleNoGameFoundException(NoGameFoundException ex) {

		return KalahaErrorResponse.builder()
				.code(ex.getCode())
				.message(ex.getMessage())
				.build();
	}

	@ExceptionHandler(Exception.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public KalahaErrorResponse handleGeneralException(Exception ex) {

		return KalahaErrorResponse.builder()
				.code("GeneralException")
				.message(ex.getMessage())
				.build();
	}
}
