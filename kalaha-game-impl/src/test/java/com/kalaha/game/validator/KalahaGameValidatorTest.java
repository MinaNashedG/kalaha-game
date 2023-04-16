package com.kalaha.game.validator;

import com.kalaha.game.exception.InvalidGameInputException;
import com.kalaha.game.exception.InvalidGameStateException;
import com.kalaha.game.exception.InvalidPlayerTurnException;
import com.kalaha.game.model.GameStatus;
import com.kalaha.game.model.KalahaGame;
import com.kalaha.game.model.KalahaGameRequest;
import com.kalaha.game.model.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class KalahaGameValidatorTest {

	public static final String PLAYER_1 = "1";
	public static final String PLAYER_2 = "2";
	private KalahaGameValidator validator;
	private KalahaGame game;

	@BeforeEach
	void setUp() {
		validator = new KalahaGameValidator();
		game = new KalahaGame();
		game.setBoard(Arrays.asList(0, 6, 6, 6, 6, 6, 6, 0, 6, 6, 6, 6, 6, 6, 0));
		game.setStartPit(0);
		game.setEndPit(6);
		game.setStatus(GameStatus.IN_PROGRESS);
		game.setNumberOfPits(6);
		game.setPlayerTurn(PLAYER_1);
		game.setPlayerTurnIndex(0);
		game.setPlayers(List.of(Player.builder().id(PLAYER_1).build(),
				Player.builder().id(PLAYER_2).build()));
	}

	@Test
	void should_not_throw_exception() {
		assertDoesNotThrow(() -> validator.validateGame(game, 1, PLAYER_1));
		assertDoesNotThrow(() -> validator.validateGameRequest(KalahaGameRequest.builder()
				.numberOfPits(5)
				.opponent(PLAYER_2)
				.build()));
		assertDoesNotThrow(() -> validator.validateGameRequest(KalahaGameRequest.builder()
				.numberOfStones(5)
				.opponent(PLAYER_2)
				.build()));
		assertDoesNotThrow(() -> validator.validateGameRequest(KalahaGameRequest.builder()
				.numberOfStones(5)
				.numberOfPits(5)
				.opponent(PLAYER_2)
				.build()));
		assertDoesNotThrow(() -> validator.validateGameRequest(KalahaGameRequest.builder()
				.opponent(PLAYER_2)
				.build()));
		assertDoesNotThrow(() -> validator.validateGameRequest(KalahaGameRequest.builder()
				.opponent(PLAYER_2)
				.build()));
	}

	@Test
	void should_throw_invalid_game_input_exception() {

		//GIVEN
		game.setStartPit(6);
		game.setEndPit(13);
		game.setPlayerTurn(PLAYER_2);
		game.setPlayerTurnIndex(1);

		assertThrows(InvalidGameInputException.class, () -> validator.validateGame(game, 7, PLAYER_2));
		assertThrows(InvalidPlayerTurnException.class, () -> validator.validateGame(game, 8, PLAYER_1));
		assertThrows(InvalidGameInputException.class, () -> validator.validateGame(game, 15, PLAYER_2));
		assertThrows(InvalidGameInputException.class, () -> validator.validateGame(game, 15, PLAYER_2));
		assertThrows(InvalidGameInputException.class, () -> validator.validateGame(game, 13, PLAYER_2));
		assertThrows(InvalidPlayerTurnException.class, () -> validator.validateGame(game, 2, PLAYER_2));

		game.setPlayerTurn(null);
		assertThrows(InvalidGameStateException.class, () -> validator.validateGame(game, 0, PLAYER_1));

		game.setPlayers(null);
		assertThrows(InvalidGameStateException.class, () -> validator.validateGame(game, 0, PLAYER_1));

	}

	@Test
	void should_throw_invalid_game_input_exception_when_request_is_invalid() {
		assertThrows(InvalidGameInputException.class, () -> validator.validateGameRequest(KalahaGameRequest.builder()
				.numberOfPits(1)
				.opponent(PLAYER_2)
				.build()));

		assertThrows(InvalidGameInputException.class, () -> validator.validateGameRequest(KalahaGameRequest.builder()
				.build()));

		assertThrows(InvalidGameInputException.class, () -> validator.validateGameRequest(KalahaGameRequest.builder()
				.numberOfPits(0)
				.opponent(PLAYER_2)
				.build()));

		assertThrows(InvalidGameInputException.class, () -> validator.validateGameRequest(KalahaGameRequest.builder()
				.numberOfPits(11)
				.opponent(PLAYER_2)
				.build()));

		assertThrows(InvalidGameInputException.class, () -> validator.validateGameRequest(KalahaGameRequest.builder()
				.numberOfStones(11)
				.opponent(PLAYER_2)
				.build()));

		assertThrows(InvalidGameInputException.class, () -> validator.validateGameRequest(KalahaGameRequest.builder()
				.numberOfStones(0)
				.opponent(PLAYER_2)
				.build()));

		assertThrows(InvalidGameInputException.class, () -> validator.validateGameRequest(KalahaGameRequest.builder()
				.opponent(PLAYER_2)
				.numberOfStones(2)
				.build()));
	}

	@Test
	void should_throw_invalid_game_input_exception_when_pit_with_zero_stones() {
		game.getBoard().set(1, 0);
		assertThrows(InvalidGameInputException.class, () -> validator.validateGame(game, 1, PLAYER_1));
	}

	@Test
	void should_throw_invalid_player_turn_exception() {
		assertThrows(InvalidPlayerTurnException.class, () -> validator.validateGame(game, 8, PLAYER_2));
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
		assertThrows(InvalidGameStateException.class, () -> validator.validateGame(game, 1, PLAYER_1));
	}

	@Test
	public void should_test_is_game_over_returns_false_for_non_empty_board() {
		// create a game with a non-empty board
		List<Integer> board = Arrays.asList(0, 3, 2, 1, 0, 1, 2, 3, 0, 4, 5, 6, 0, 8);
		KalahaGame game = new KalahaGame();
		game.setBoard(board);
		game.setNumberOfPits(6);
		game.setStartPit(0);
		game.setEndPit(6);
		// assert that isGameOver returns false
		assertFalse(validator.isGameOver(game));
	}

	@Test
	public void should_test_is_game_over_returns_true_for_empty_board() {
		// create a game with an empty board
		List<Integer> board = Arrays.asList(0, 0, 0, 0, 0, 0, 6, 6, 6, 6, 0, 0, 6, 6);
		KalahaGame game = new KalahaGame();
		game.setBoard(board);
		game.setNumberOfPits(6);
		game.setStartPit(7);
		game.setEndPit(13);
		// assert that isGameOver returns true
		assertTrue(validator.isGameOver(game));
	}

	@Test
	public void should_test_is_game_over_returns_false_for_partially_empty_board() {
		// create a game with a partially empty board
		List<Integer> board = Arrays.asList(0, 0, 0, 0, 0, 7, 7, 0, 0, 1, 7, 7, 7, 7);
		KalahaGame game = new KalahaGame();
		game.setBoard(board);
		game.setNumberOfPits(6);
		game.setStartPit(0);
		game.setEndPit(6);
		// assert that isGameOver returns false
		assertFalse(validator.isGameOver(game));
	}

}