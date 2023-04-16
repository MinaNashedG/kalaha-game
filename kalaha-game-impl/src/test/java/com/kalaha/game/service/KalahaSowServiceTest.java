package com.kalaha.game.service;

import com.kalaha.game.dao.KalahaGameRepository;
import com.kalaha.game.mapper.KalahaGameMapper;
import com.kalaha.game.model.GameStatus;
import com.kalaha.game.model.KalahaGame;
import com.kalaha.game.model.KalahaGameResponse;
import com.kalaha.game.model.Player;
import com.kalaha.game.model.PlayerResponse;
import com.kalaha.game.model.Status;
import com.kalaha.game.security.UserContext;
import com.kalaha.game.validator.KalahaGameValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class KalahaSowServiceTest {

	public static final String GAME_ID = "123";
	public static final String PLAYER_ONE = "1";
	public static final String PLAYER_TWO = "2";
	public static final List<PlayerResponse> PLAYER_RESPONSE_LIST = List.of(
			PlayerResponse.builder().id(PLAYER_ONE).build(), PlayerResponse.builder().id(PLAYER_TWO).build());
	public static final List<Player> PLAYERS = List.of(Player.builder().id(PLAYER_ONE).build(),
			Player.builder().id(PLAYER_TWO).build());
	@Mock
	private KalahaGameRepository kalahaGameRepository;
	@Mock
	private KalahaGameMapper kalahaGameMapper;
	@Mock
	private KalahaGameValidator kalahaGameValidator;

	@Mock
	private UserContext userContext;

	private KalahaSowService kalahaSowService;

	@BeforeEach
	void setUp() {
		kalahaSowService = new KalahaSowService(kalahaGameRepository, kalahaGameMapper, kalahaGameValidator,
				userContext);
		when(userContext.getUserId()).thenReturn(PLAYER_ONE);
	}

	@Test
	void should_sow_stones_when_pit_id_0_and_return_bonus_turn_player_one() {
		//GIVEN
		List<Integer> board = Arrays.asList(6, 6, 6, 6, 6, 6, 0, 6, 6, 6, 6, 6, 6, 0);
		List<Integer> expectedBoard = Arrays.asList(0, 7, 7, 7, 7, 7, 1, 6, 6, 6, 6, 6, 6, 0);

		KalahaGameResponse gameResponse = kalahaGameResponse(expectedBoard, PLAYER_ONE);
		KalahaGame kalahaGame = kalahaGamePlayerOneTurn(board, false, GameStatus.NEW);
		KalahaGame expectedBonusTurn = kalahaGamePlayerOneTurn(expectedBoard, true, GameStatus.IN_PROGRESS);

		//WHEN
		when(kalahaGameRepository.findById(GAME_ID)).thenReturn(Optional.of(kalahaGame));
		when(kalahaGameValidator.isGameOver(kalahaGamePlayerOneTurn(expectedBoard, true, GameStatus.NEW)))
				.thenReturn(false);
		when(kalahaGameRepository.save(expectedBonusTurn)).thenReturn(expectedBonusTurn);
		when(kalahaGameMapper.transform(expectedBonusTurn)).thenReturn(gameResponse);

		//THEN
		assertEquals(gameResponse, kalahaSowService.sow(GAME_ID, 0));

	}

	@Test
	void should_sow_stones_when_pit_id_1_and_turn_player_one() {
		//GIVEN
		List<Integer> board = Arrays.asList(6, 6, 6, 6, 6, 6, 0, 6, 6, 6, 6, 6, 6, 0);
		List<Integer> expectedBoard = Arrays.asList(6, 0, 7, 7, 7, 7, 1, 7, 6, 6, 6, 6, 6, 0);

		KalahaGameResponse gameResponse = kalahaGameResponse(expectedBoard, PLAYER_TWO);
		KalahaGame expected = kalahaGamePlayerTwoTurn(expectedBoard, false, GameStatus.IN_PROGRESS);

		//WHEN
		when(kalahaGameRepository.findById(GAME_ID))
				.thenReturn(Optional.of(kalahaGamePlayerOneTurn(board, false, GameStatus.IN_PROGRESS)));

		when(kalahaGameValidator.isGameOver(kalahaGamePlayerOneTurn(expectedBoard, false,
				GameStatus.IN_PROGRESS))).thenReturn(false);
		when(kalahaGameRepository.save(expected)).thenReturn(expected);
		when(kalahaGameMapper.transform(expected)).thenReturn(gameResponse);

		//THEN
		assertEquals(gameResponse, kalahaSowService.sow(GAME_ID, 1));

	}

	@Test
	void should_sow_stones_when_player_turn_two_and_pit_id_12() {
		//GIVEN
		List<Integer> board = Arrays.asList(0, 7, 7, 7, 7, 7, 1, 6, 6, 6, 6, 6, 6, 0);
		List<Integer> expectedBoard = Arrays.asList(1, 8, 8, 8, 8, 7, 1, 6, 6, 6, 6, 6, 0, 1);

		KalahaGameResponse gameResponse = kalahaGameResponse(expectedBoard, PLAYER_ONE);
		KalahaGame expected = kalahaGamePlayerOneTurn(expectedBoard, false, GameStatus.IN_PROGRESS);

		//WHEN
		when(kalahaGameRepository.findById(GAME_ID)).thenReturn(
				Optional.of(kalahaGamePlayerTwoTurn(board, false, GameStatus.IN_PROGRESS)));
		when(kalahaGameValidator.isGameOver(
				kalahaGamePlayerTwoTurn(expectedBoard, false, GameStatus.IN_PROGRESS))).thenReturn(false);
		when(kalahaGameRepository.save(expected)).thenReturn(expected);
		when(kalahaGameMapper.transform(expected)).thenReturn(gameResponse);

		//THEN
		assertEquals(gameResponse, kalahaSowService.sow(GAME_ID, 12));
	}

	@Test
	void should_capture_stones_when_player_turn_two_and_pit_10_opposite_2() {
		//GIVEN
		List<Integer> board = Arrays.asList(0, 7, 8, 7, 7, 7, 20, 6, 6, 1, 0, 6, 6, 10);
		List<Integer> expectedBoard = Arrays.asList(0, 7, 0, 7, 7, 7, 20, 6, 6, 0, 0, 6, 6, 19);

		KalahaGameResponse gameResponse = kalahaGameResponse(expectedBoard, PLAYER_ONE);
		KalahaGame expected = kalahaGamePlayerOneTurn(expectedBoard, false, GameStatus.IN_PROGRESS);

		//WHEN
		when(kalahaGameRepository.findById(GAME_ID)).thenReturn(
				Optional.of(kalahaGamePlayerTwoTurn(board, false, GameStatus.IN_PROGRESS)));
		when(kalahaGameValidator.isGameOver(
				kalahaGamePlayerTwoTurn(expectedBoard, false, GameStatus.IN_PROGRESS))).thenReturn(false);

		when(kalahaGameRepository.save(expected)).thenReturn(expected);
		when(kalahaGameMapper.transform(expected)).thenReturn(gameResponse);

		//THEN
		assertEquals(gameResponse, kalahaSowService.sow(GAME_ID, 9));
	}

	@Test
	void should_not_capture_stones_when_player_turn_two_and_pit_10_opposite_2_with_zero_stones() {
		//GIVEN
		List<Integer> board = Arrays.asList(0, 7, 0, 7, 7, 7, 20, 6, 6, 1, 0, 6, 6, 10);
		List<Integer> expectedBoard = Arrays.asList(0, 7, 0, 7, 7, 7, 20, 6, 6, 0, 1, 6, 6, 10);

		KalahaGameResponse gameResponse = kalahaGameResponse(expectedBoard, PLAYER_ONE);

		//WHEN
		when(kalahaGameRepository.findById(GAME_ID)).thenReturn(
				Optional.of(kalahaGamePlayerTwoTurn(board, false, GameStatus.IN_PROGRESS)));
		when(kalahaGameValidator.isGameOver(
				kalahaGamePlayerTwoTurn(expectedBoard, false, GameStatus.IN_PROGRESS))).thenReturn(false);

		//Switch to another player
		KalahaGame expected = kalahaGamePlayerOneTurn(expectedBoard, false, GameStatus.IN_PROGRESS);

		when(kalahaGameRepository.save(expected)).thenReturn(expected);
		when(kalahaGameMapper.transform(expected)).thenReturn(gameResponse);

		//THEN
		assertEquals(gameResponse, kalahaSowService.sow(GAME_ID, 9));
	}

	@Test
	void should_change_game_status_over_and_return_winner_player_one() {

		//GIVEN
		List<Integer> board = Arrays.asList(5, 5, 5, 5, 5, 5, 20, 0, 0, 0, 0, 0, 1, 10);
		List<Integer> expectedBoard = Arrays.asList(5, 5, 5, 5, 5, 5, 20, 0, 0, 0, 0, 0, 0, 11);

		KalahaGameResponse gameResponse = KalahaGameResponse.builder()
				.board(board)
				.playerTurn(PLAYER_ONE)
				.id(GAME_ID)
				.bonusTurn(true)
				.status(Status.OVER)
				.winner(PLAYER_ONE)
				.players(PLAYER_RESPONSE_LIST)
				.build();

		KalahaGame expectedKalahaGameOver = KalahaGame.builder().board(board)
				.startPit(7)
				.endPit(13)
				.numberOfStones(6)
				.playerTurn(PLAYER_TWO)
				.playerTurnIndex(1)
				.numberOfPlayers(2)
				.bonusTurn(true)
				.numberOfPits(6)
				.status(GameStatus.OVER)
				.players(PLAYERS)
				.id(GAME_ID)
				.winner(PLAYER_ONE)
				.build();
		//WHEN
		when(kalahaGameRepository.findById(GAME_ID)).thenReturn(
				Optional.of(kalahaGamePlayerTwoTurn(board, false, GameStatus.IN_PROGRESS)));
		when(kalahaGameValidator.isGameOver(
				kalahaGamePlayerTwoTurn(expectedBoard, true, GameStatus.IN_PROGRESS))).thenReturn(true);
		when(kalahaGameRepository.save(expectedKalahaGameOver)).thenReturn(expectedKalahaGameOver);
		when(kalahaGameMapper.transform(expectedKalahaGameOver)).thenReturn(gameResponse);

		assertEquals(gameResponse, kalahaSowService.sow(GAME_ID, 12));
	}

	private KalahaGame kalahaGamePlayerOneTurn(List<Integer> board, boolean bonus, GameStatus status) {

		return KalahaGame.builder().board(board)
				.startPit(0)
				.endPit(6)
				.numberOfStones(6)
				.playerTurn(PLAYER_ONE)
				.playerTurnIndex(0)
				.numberOfPlayers(2)
				.bonusTurn(bonus)
				.numberOfPits(6)
				.status(status)
				.players(PLAYERS)
				.id(GAME_ID).build();
	}

	private KalahaGame kalahaGamePlayerTwoTurn(List<Integer> board, boolean bonus, GameStatus status) {

		return KalahaGame.builder().board(board)
				.startPit(7)
				.endPit(13)
				.numberOfStones(6)
				.playerTurn(PLAYER_TWO)
				.playerTurnIndex(1)
				.numberOfPlayers(2)
				.bonusTurn(bonus)
				.numberOfPits(6)
				.status(status)
				.players(PLAYERS)
				.id(GAME_ID).build();
	}

	private KalahaGameResponse kalahaGameResponse(List<Integer> board, String turn) {
		return KalahaGameResponse.builder()
				.board(board)
				.playerTurn(turn)
				.id(GAME_ID)
				.bonusTurn(true)
				.status(Status.IN_PROGRESS)
				.players(PLAYER_RESPONSE_LIST)
				.build();
	}
}