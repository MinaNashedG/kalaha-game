package com.kalaha.game.service;

import com.kalaha.game.dao.KalahaGameRepository;
import com.kalaha.game.mapper.KalahaGameMapper;
import com.kalaha.game.model.GameStatus;
import com.kalaha.game.model.KalahaGame;
import com.kalaha.game.model.KalahaGameResponse;
import com.kalaha.game.model.Status;
import com.kalaha.game.validator.KalahaGameValidator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class KalahaSowServiceTest {

	public static final String GAME_ID = "123";
	@Mock
	private KalahaGameRepository kalahaGameRepository;
	@Mock
	private KalahaGameMapper kalahaGameMapper;
	@Mock
	private KalahaGameValidator kalahaGameValidator;

	private KalahaSowService kalahaSowService;

	@BeforeEach
	void setUp() {
		kalahaSowService = new KalahaSowService(kalahaGameRepository, kalahaGameMapper, kalahaGameValidator);
	}

	@Test
	void should_sow_stones_when_pit_id_0_and_return_bonus_turn_player_one() {
		//GIVEN
		List<Integer> board = Arrays.asList(6, 6, 6, 6, 6, 6, 0, 6, 6, 6, 6, 6, 6, 0);
		List<Integer> expectedBoard = Arrays.asList(0, 7, 7, 7, 7, 7, 1, 6, 6, 6, 6, 6, 6, 0);
		KalahaGameResponse expectedGameResponse = kalahaGameResponse(expectedBoard, 1);
		KalahaGame kalahaGame = kalahaGamePlayerOneTurn(board, false, GameStatus.NEW);
		KalahaGame kalahaGameWithBonusTurn = kalahaGamePlayerOneTurn(expectedBoard, true, GameStatus.NEW);
		KalahaGame expectedKalahaGame = kalahaGamePlayerOneTurn(expectedBoard, true, GameStatus.IN_PROGRESS);

		//WHEN
		Mockito.when(kalahaGameRepository.findById(GAME_ID)).thenReturn(Optional.of(kalahaGame));
		Mockito.when(kalahaGameValidator.isGameOver(kalahaGameWithBonusTurn)).thenReturn(false);
		Mockito.when(kalahaGameRepository.save(expectedKalahaGame)).thenReturn(expectedKalahaGame);
		Mockito.when(kalahaGameMapper.transform(expectedKalahaGame)).thenReturn(expectedGameResponse);

		//THEN
		Assertions.assertEquals(expectedGameResponse, kalahaSowService.sow(GAME_ID, 0));
	}

	@Test
	void should_sow_stones_when_pit_id_1_and_turn_player_one() {
		//GIVEN
		List<Integer> board = Arrays.asList(6, 6, 6, 6, 6, 6, 0, 6, 6, 6, 6, 6, 6, 0);
		List<Integer> expectedBoard = Arrays.asList(6, 0, 7, 7, 7, 7, 1, 7, 6, 6, 6, 6, 6, 0);
		KalahaGameResponse expectedGameResponse = kalahaGameResponse(expectedBoard, 2);
		KalahaGame kalahaGame = kalahaGamePlayerOneTurn(board, false, GameStatus.IN_PROGRESS);
		KalahaGame input = kalahaGamePlayerOneTurn(expectedBoard, false, GameStatus.IN_PROGRESS);

		//WHEN
		Mockito.when(kalahaGameRepository.findById(GAME_ID)).thenReturn(Optional.of(kalahaGame));

		//Switch to another player
		KalahaGame expected = kalahaGamePlayerTwoTurn(expectedBoard, false, GameStatus.IN_PROGRESS);

		Mockito.when(kalahaGameValidator.isGameOver(input)).thenReturn(false);
		Mockito.when(kalahaGameRepository.save(expected)).thenReturn(expected);
		Mockito.when(kalahaGameMapper.transform(expected)).thenReturn(expectedGameResponse);

		//THEN
		Assertions.assertEquals(expectedGameResponse, kalahaSowService.sow(GAME_ID, 1));

	}

	@Test
	void should_sow_stones_when_player_turn_two_and_pit_id_12() {
		//GIVEN
		List<Integer> board = Arrays.asList(0, 7, 7, 7, 7, 7, 1, 6, 6, 6, 6, 6, 6, 0);
		List<Integer> expectedBoard = Arrays.asList(1, 8, 8, 8, 8, 7, 1, 6, 6, 6, 6, 6, 0, 1);
		KalahaGameResponse expectedGameResponse = kalahaGameResponse(expectedBoard, 1);
		KalahaGame kalahaGame = kalahaGamePlayerTwoTurn(board, false, GameStatus.IN_PROGRESS);
		KalahaGame input = kalahaGamePlayerTwoTurn(expectedBoard, false, GameStatus.IN_PROGRESS);

		//WHEN
		Mockito.when(kalahaGameRepository.findById(GAME_ID)).thenReturn(Optional.of(kalahaGame));
		Mockito.when(kalahaGameValidator.isGameOver(input)).thenReturn(false);

		//Switch to another player
		KalahaGame expected = kalahaGamePlayerOneTurn(expectedBoard, false, GameStatus.IN_PROGRESS);

		Mockito.when(kalahaGameRepository.save(expected)).thenReturn(expected);
		Mockito.when(kalahaGameMapper.transform(expected)).thenReturn(expectedGameResponse);

		//THEN
		Assertions.assertEquals(expectedGameResponse, kalahaSowService.sow(GAME_ID, 12));
	}

	@Test
	void should_capture_stones_when_player_turn_two_and_pit_10_opposite_2() {
		//GIVEN
		List<Integer> board = Arrays.asList(0, 7, 8, 7, 7, 7, 20, 6, 6, 1, 0, 6, 6, 10);
		List<Integer> expectedBoard = Arrays.asList(0, 7, 0, 7, 7, 7, 20, 6, 6, 0, 0, 6, 6, 19);
		KalahaGameResponse expectedGameResponse = kalahaGameResponse(expectedBoard, 1);
		KalahaGame kalahaGame = kalahaGamePlayerTwoTurn(board, false, GameStatus.IN_PROGRESS);
		KalahaGame input = kalahaGamePlayerTwoTurn(expectedBoard, false, GameStatus.IN_PROGRESS);

		//WHEN
		Mockito.when(kalahaGameRepository.findById(GAME_ID)).thenReturn(Optional.of(kalahaGame));
		Mockito.when(kalahaGameValidator.isGameOver(input)).thenReturn(false);

		//Switch to another player
		KalahaGame expected = kalahaGamePlayerOneTurn(expectedBoard, false, GameStatus.IN_PROGRESS);

		Mockito.when(kalahaGameRepository.save(expected)).thenReturn(expected);
		Mockito.when(kalahaGameMapper.transform(expected)).thenReturn(expectedGameResponse);

		//THEN
		Assertions.assertEquals(expectedGameResponse, kalahaSowService.sow(GAME_ID, 9));
	}

	@Test
	void should_not_capture_stones_when_player_turn_two_and_pit_10_opposite_2_with_zero_stones() {
		//GIVEN
		List<Integer> board = Arrays.asList(0, 7, 0, 7, 7, 7, 20, 6, 6, 1, 0, 6, 6, 10);
		List<Integer> expectedBoard = Arrays.asList(0, 7, 0, 7, 7, 7, 20, 6, 6, 0, 1, 6, 6, 10);
		KalahaGameResponse expectedGameResponse = kalahaGameResponse(expectedBoard, 1);
		KalahaGame kalahaGame = kalahaGamePlayerTwoTurn(board, false, GameStatus.IN_PROGRESS);
		KalahaGame input = kalahaGamePlayerTwoTurn(expectedBoard, false, GameStatus.IN_PROGRESS);

		//WHEN
		Mockito.when(kalahaGameRepository.findById(GAME_ID)).thenReturn(Optional.of(kalahaGame));
		Mockito.when(kalahaGameValidator.isGameOver(input)).thenReturn(false);

		//Switch to another player
		KalahaGame expected = kalahaGamePlayerOneTurn(expectedBoard, false, GameStatus.IN_PROGRESS);

		Mockito.when(kalahaGameRepository.save(expected)).thenReturn(expected);
		Mockito.when(kalahaGameMapper.transform(expected)).thenReturn(expectedGameResponse);

		//THEN
		Assertions.assertEquals(expectedGameResponse, kalahaSowService.sow(GAME_ID, 9));
	}

	@Test
	void should_change_game_status_over_and_return_winner_player_one() {

		//GIVEN
		List<Integer> board = Arrays.asList(5, 5, 5, 5, 5, 5, 20, 0, 0, 0, 0, 0, 1, 10);
		List<Integer> expectedBoard = Arrays.asList(5, 5, 5, 5, 5, 5, 20, 0, 0, 0, 0, 0, 0, 11);
		KalahaGameResponse expectedGameResponse = kalahaGameResponse(expectedBoard, 1);
		expectedGameResponse.setStatus(Status.OVER);
		expectedGameResponse.setPlayerWin(1);

		KalahaGame kalahaGame = kalahaGamePlayerTwoTurn(board, false, GameStatus.IN_PROGRESS);
		KalahaGame expectedKalahaGame = kalahaGamePlayerTwoTurn(expectedBoard, true, GameStatus.IN_PROGRESS);
		KalahaGame expectedKalahaGameOver = kalahaGamePlayerTwoTurn(expectedBoard, true, GameStatus.OVER);
		expectedKalahaGameOver.setPlayerWin(1);
		//WHEN
		Mockito.when(kalahaGameRepository.findById(GAME_ID)).thenReturn(Optional.of(kalahaGame));
		Mockito.when(kalahaGameValidator.isGameOver(expectedKalahaGame)).thenReturn(true);
		Mockito.when(kalahaGameRepository.save(expectedKalahaGameOver)).thenReturn(expectedKalahaGameOver);
		Mockito.when(kalahaGameMapper.transform(expectedKalahaGameOver)).thenReturn(expectedGameResponse);

		Assertions.assertEquals(expectedGameResponse, kalahaSowService.sow(GAME_ID, 12));
	}

	private KalahaGame kalahaGamePlayerOneTurn(List<Integer> board, boolean bonus, GameStatus status) {

		return KalahaGame.builder().board(board)
				.startPit(0)
				.endPit(6)
				.numberOfStones(6)
				.playerTurn(1)
				.numberOfPlayers(2)
				.bonusTurn(bonus)
				.numberOfPits(6)
				.status(status)
				.id(GAME_ID).build();
	}

	private KalahaGame kalahaGamePlayerTwoTurn(List<Integer> board, boolean bonus, GameStatus status) {

		return KalahaGame.builder().board(board)
				.startPit(7)
				.endPit(13)
				.numberOfStones(6)
				.playerTurn(2)
				.numberOfPlayers(2)
				.bonusTurn(bonus)
				.numberOfPits(6)
				.status(status)
				.id(GAME_ID).build();
	}

	private KalahaGameResponse kalahaGameResponse(List<Integer> board, int turn) {
		return KalahaGameResponse.builder()
				.board(board)
				.playerTurn(turn)
				.id(GAME_ID)
				.bonusTurn(true)
				.status(Status.IN_PROGRESS)
				.build();
	}
}