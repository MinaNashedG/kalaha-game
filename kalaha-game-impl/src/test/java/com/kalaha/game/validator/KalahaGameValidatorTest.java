package com.kalaha.game.validator;

import com.kalaha.game.exception.InvalidGameInputException;
import com.kalaha.game.exception.InvalidGameStateException;
import com.kalaha.game.exception.InvalidPlayerTurnException;
import com.kalaha.game.model.GameStatus;
import com.kalaha.game.model.KalahaGame;
import com.kalaha.game.model.KalahaGameRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class KalahaGameValidatorTest {

	private KalahaGameValidator validator;
	private KalahaGame game;

	@BeforeEach
	void setUp() {
		validator = new KalahaGameValidator();
		game = new KalahaGame();
		game.setBoard(Arrays.asList(0, 6, 6, 6, 6, 6, 6, 0, 6, 6, 6, 6, 6, 6, 0));
		game.setStartPit(1);
		game.setEndPit(7);
		game.setStatus(GameStatus.IN_PROGRESS);
		game.setNumberOfPits(6);
	}

	@Test
	void should_not_throw_exception() {
		assertDoesNotThrow(() -> validator.validateGameAndPit(game, 1));
		assertDoesNotThrow(() -> validator.validateGameRequest(KalahaGameRequest.builder()
				.numberOfPits(5)
				.build()));
		assertDoesNotThrow(() -> validator.validateGameRequest(KalahaGameRequest.builder()
				.numberOfStones(5)
				.build()));
		assertDoesNotThrow(() -> validator.validateGameRequest(KalahaGameRequest.builder()
				.numberOfStones(5)
				.numberOfPits(5)
				.build()));
		assertDoesNotThrow(() -> validator.validateGameRequest(KalahaGameRequest.builder()
				.build()));
		assertDoesNotThrow(() -> validator.validateGameRequest(KalahaGameRequest.builder()
				.build()));
	}

	@Test
	void should_throw_invalid_game_input_exception() {
		assertThrows(InvalidGameInputException.class, () -> validator.validateGameAndPit(game, -1));
		assertThrows(InvalidGameInputException.class, () -> validator.validateGameAndPit(game, 7));
		assertThrows(InvalidPlayerTurnException.class, () -> validator.validateGameAndPit(game, 8));
		assertThrows(InvalidGameInputException.class, () -> validator.validateGameAndPit(game, 15));
		assertThrows(InvalidGameInputException.class, () -> validator.validateGameAndPit(game, 15));
	}

	@Test
	void should_throw_invalid_game_input_exception_when_request_is_invalid() {
		assertThrows(InvalidGameInputException.class, () -> validator.validateGameRequest(KalahaGameRequest.builder()
				.numberOfPits(1)
				.build()));
		assertThrows(InvalidGameInputException.class, () -> validator.validateGameRequest(KalahaGameRequest.builder()
				.numberOfPits(0)
				.build()));
		assertThrows(InvalidGameInputException.class, () -> validator.validateGameRequest(KalahaGameRequest.builder()
				.numberOfPits(11)
				.build()));
		assertThrows(InvalidGameInputException.class, () -> validator.validateGameRequest(KalahaGameRequest.builder()
				.numberOfStones(11)
				.build()));
		assertThrows(InvalidGameInputException.class, () -> validator.validateGameRequest(KalahaGameRequest.builder()
				.numberOfStones(0)
				.build()));
		assertThrows(InvalidGameInputException.class, () -> validator.validateGameRequest(KalahaGameRequest.builder()
				.numberOfStones(2)
				.build()));
	}

	@Test
	void should_throw_invalid_game_input_exception_when_pit_with_zero_stones() {
		game.getBoard().set(1, 0);
		assertThrows(InvalidGameInputException.class, () -> validator.validateGameAndPit(game, 1));
	}

	@Test
	void should_throw_invalid_player_turn_exception() {
		assertThrows(InvalidPlayerTurnException.class, () -> validator.validateGameAndPit(game, 8));
	}

	@Test
	void should_return_true_when_game_over() {
		game.getBoard().set(1, 0);
		game.getBoard().set(2, 0);
		game.getBoard().set(3, 0);
		game.getBoard().set(4, 0);
		game.getBoard().set(5, 0);
		game.getBoard().set(6, 0);
		assertTrue(validator.isGameOver(game));
	}

	@Test
	void should_return_false_when_call_is_game_over() {
		assertFalse(validator.isGameOver(game));
	}

	@Test
	void should_throw_invalid_game_state_exception_when_game_is_already_over() {
		game.setStatus(GameStatus.OVER);
		assertThrows(InvalidGameStateException.class, () -> validator.validateGameAndPit(game, 1));
	}

}