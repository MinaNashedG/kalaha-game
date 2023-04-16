package com.kalaha.game.service;

import com.kalaha.game.config.KalahaGameConfig;
import com.kalaha.game.dao.KalahaGameRepository;
import com.kalaha.game.dao.KalahaPlayerRepository;
import com.kalaha.game.exception.InvalidGameInputException;
import com.kalaha.game.mapper.KalahaGameMapper;
import com.kalaha.game.model.GameStatus;
import com.kalaha.game.model.KalahaGame;
import com.kalaha.game.model.KalahaGameRequest;
import com.kalaha.game.model.KalahaGameResponse;
import com.kalaha.game.model.Player;
import com.kalaha.game.security.UserContext;
import com.kalaha.game.validator.KalahaGameValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class KalahaGameServiceTest {
	public static final String OPPONENT = "2";
	public static final String PLAYER_ONE = "1";
	private KalahaGameService kalahaGameService;

	@Mock
	private KalahaGameRepository kalahaGameRepository;
	@Mock
	private KalahaGameMapper kalahaGameMapper;
	@Mock
	private KalahaGameConfig kalahaGameConfig;

	@Mock
	private KalahaGameValidator kalahaGameValidator;

	@Mock
	private KalahaPlayerRepository kalahaPlayerRepository;

	@Mock
	private UserContext userContext;

	@BeforeEach
	void setUp() {
		kalahaGameService = new KalahaGameService(kalahaGameRepository, kalahaGameMapper, kalahaGameConfig,
				kalahaGameValidator, kalahaPlayerRepository, userContext);

		lenient().when(kalahaPlayerRepository.findById(OPPONENT))
				.thenReturn(Optional.ofNullable(Player.builder().id(OPPONENT).build()));

		lenient().when(kalahaPlayerRepository.findById(PLAYER_ONE))
				.thenReturn(Optional.ofNullable(Player.builder().id(PLAYER_ONE).build()));

		lenient().when(userContext.getUserId())
				.thenReturn(PLAYER_ONE);
	}

	@Test
	void should_create_new_game() {
		// Given
		KalahaGameRequest request = new KalahaGameRequest();
		request.setNumberOfPits(6);
		request.setNumberOfStones(6);
		request.setOpponent(OPPONENT);
		// When
		KalahaGame game = KalahaGame.builder()
				.board(List.of(6, 6, 6, 6, 6, 6, 0, 6, 6, 6, 6, 6, 6, 0))
				.status(GameStatus.NEW)
				.numberOfPits(6)
				.numberOfPlayers(2)
				.players(List.of(Player.builder().id(PLAYER_ONE).build(),
						Player.builder().id(OPPONENT).build()))
				.numberOfStones(6)
				.playerTurn(PLAYER_ONE)
				.playerTurnIndex(0)
				.startPit(0)
				.endPit(6)
				.build();
		when(kalahaGameRepository.save(game)).thenReturn(game);
		KalahaGameResponse expected = new KalahaGameResponse();
		when(kalahaGameMapper.transform(game)).thenReturn(expected);
		when(kalahaGameConfig.getDefaultPlayers()).thenReturn(2);
		KalahaGameResponse actual = kalahaGameService.createNewGame(request);
		// Then
		assertThat(actual).isEqualTo(expected);
	}

	@Test
	void should_throwInvalidGameInputException() {
		//GIVEN
		lenient().when(userContext.getUserId()).thenReturn(null);

		//THEN
		assertThrows(InvalidGameInputException.class, () -> kalahaGameService.createNewGame(null));
		assertThrows(InvalidGameInputException.class, () -> kalahaGameService.createNewGame(KalahaGameRequest
				.builder().build()));
		assertThrows(InvalidGameInputException.class, () -> kalahaGameService.createNewGame(KalahaGameRequest
				.builder().opponent("3").build()));
		assertThrows(InvalidGameInputException.class, () -> kalahaGameService.createNewGame(KalahaGameRequest
				.builder().opponent("3").build()));
	}

	@Test
	void should_createNewGameWithEmptyInput() {
		// When
		KalahaGame game = KalahaGame.builder()
				.board(List.of(6, 6, 6, 6, 6, 6, 0, 6, 6, 6, 6, 6, 6, 0))
				.status(GameStatus.NEW)
				.numberOfPits(6)
				.numberOfPlayers(2)
				.players(List.of(Player.builder().id(PLAYER_ONE).build(),
						Player.builder().id(OPPONENT).build()))
				.numberOfStones(6)
				.playerTurn(KalahaGameServiceTest.PLAYER_ONE)
				.playerTurnIndex(0)
				.startPit(0)
				.endPit(6)
				.build();
		when(kalahaGameRepository.save(game)).thenReturn(game);
		KalahaGameResponse expected = new KalahaGameResponse();
		when(kalahaGameMapper.transform(game)).thenReturn(expected);
		when(kalahaGameConfig.getDefaultPits()).thenReturn(6);
		when(kalahaGameConfig.getDefaultPlayers()).thenReturn(2);
		when(kalahaGameConfig.getDefaultStones()).thenReturn(6);

		//Then
		final KalahaGameResponse actual = kalahaGameService.createNewGame(KalahaGameRequest.builder()
				.opponent(OPPONENT)
				.build());

		assertThat(actual).isEqualTo(expected);
	}
}