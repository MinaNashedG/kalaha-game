package com.kalaha.game.handler;

import com.kalaha.game.exception.AuthenticationFailedException;
import com.kalaha.game.exception.InvalidGameInputException;
import com.kalaha.game.exception.InvalidGameStateException;
import com.kalaha.game.exception.InvalidPlayerTurnException;
import com.kalaha.game.exception.KalahaException;
import com.kalaha.game.exception.NoGameFoundException;
import com.kalaha.game.exception.NoUserFoundException;
import com.kalaha.game.exception.PlayerAlreadyExistsException;
import com.kalaha.game.model.KalahaErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GeneralExceptionHandler {

	@ExceptionHandler({InvalidGameInputException.class, InvalidPlayerTurnException.class,
			InvalidGameStateException.class, PlayerAlreadyExistsException.class})
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public KalahaErrorResponse handle(KalahaException ex) {
		log.error("Invalid input params,bad request ", ex);
		return KalahaErrorResponse.builder()
				.code(ex.getCode())
				.message(ex.getMessage())
				.build();
	}

	@ExceptionHandler({NoGameFoundException.class, NoUserFoundException.class})
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public KalahaErrorResponse handleNoFoundException(NoGameFoundException ex) {
		log.error("No resource found ,bad request ", ex);
		return KalahaErrorResponse.builder()
				.code(ex.getCode())
				.message(ex.getMessage())
				.build();
	}

	@ExceptionHandler({AuthenticationFailedException.class})
	@ResponseStatus(HttpStatus.UNAUTHORIZED)
	public KalahaErrorResponse unauthorized(AuthenticationFailedException ex) {
		log.error("fail to authenticate user", ex);
		return KalahaErrorResponse.builder()
				.code(ex.getCode())
				.message(ex.getMessage())
				.build();
	}

	@ExceptionHandler(Exception.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public KalahaErrorResponse handleGeneralException(Exception ex) {
		log.error("internal server error", ex);
		return KalahaErrorResponse.builder()
				.code("GeneralException")
				.message("Internal server error ")
				.build();
	}
}
