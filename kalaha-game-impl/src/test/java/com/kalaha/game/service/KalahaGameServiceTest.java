package com.kalaha.game.service;

import com.kalaha.game.config.KalahaGameConfig;
import com.kalaha.game.dao.KalahaGameRepository;
import com.kalaha.game.mapper.KalahaGameMapper;
import com.kalaha.game.model.GameStatus;
import com.kalaha.game.model.KalahaGame;
import com.kalaha.game.model.KalahaGameRequest;
import com.kalaha.game.model.KalahaGameResponse;
import com.kalaha.game.validator.KalahaGameValidator;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

@ExtendWith(MockitoExtension.class)
class KalahaGameServiceTest {
	private KalahaGameService kalahaGameService;

	@Mock
	private KalahaGameRepository kalahaGameRepository;
	@Mock
	private KalahaGameMapper kalahaGameMapper;
	@Mock
	private KalahaGameConfig kalahaGameConfig;

	@Mock
	private KalahaGameValidator kalahaGameValidator;

	@BeforeEach
	void setUp() {
		kalahaGameService = new KalahaGameService(kalahaGameRepository, kalahaGameMapper, kalahaGameConfig,
				kalahaGameValidator);
	}

	@Test
	@DisplayName("Test createNewGame method")
	void should_create_new_game() {
		// Given
		KalahaGameRequest request = new KalahaGameRequest();
		request.setNumberOfPits(6);
		request.setNumberOfStones(6);
		// When
		KalahaGame game = KalahaGame.builder()
				.board(List.of(6, 6, 6, 6, 6, 6, 0, 6, 6, 6, 6, 6, 6, 0))
				.status(GameStatus.NEW)
				.numberOfPits(6)
				.numberOfPlayers(2)
				.numberOfStones(6)
				.playerTurn(1)
				.startPit(0)
				.endPit(6)
				.build();
		Mockito.when(kalahaGameRepository.save(game)).thenReturn(game);
		KalahaGameResponse expected = new KalahaGameResponse();
		Mockito.when(kalahaGameMapper.transform(game)).thenReturn(expected);
		Mockito.when(kalahaGameConfig.getDefaultPlayers()).thenReturn(2);
		KalahaGameResponse actual = kalahaGameService.createNewGame(request);
		// Then
		Assertions.assertThat(actual).isEqualTo(expected);
	}

	@Test
	@DisplayName("Test createNewGame method with null input")
	void testCreateNewGameWithNullInput() {
		// When
		KalahaGame game = KalahaGame.builder()
				.board(List.of(6, 6, 6, 6, 6, 6, 0, 6, 6, 6, 6, 6, 6, 0))
				.status(GameStatus.NEW)
				.numberOfPits(6)
				.numberOfPlayers(2)
				.numberOfStones(6)
				.playerTurn(1)
				.startPit(0)
				.endPit(6)
				.build();
		Mockito.when(kalahaGameRepository.save(game)).thenReturn(game);
		KalahaGameResponse expected = new KalahaGameResponse();
		Mockito.when(kalahaGameMapper.transform(game)).thenReturn(expected);
		Mockito.when(kalahaGameConfig.getDefaultPits()).thenReturn(6);
		Mockito.when(kalahaGameConfig.getDefaultPlayers()).thenReturn(2);
		Mockito.when(kalahaGameConfig.getDefaultStones()).thenReturn(6);
		KalahaGameResponse actual = kalahaGameService.createNewGame(null);
		// Then
		Assertions.assertThat(actual).isEqualTo(expected);

	}
}