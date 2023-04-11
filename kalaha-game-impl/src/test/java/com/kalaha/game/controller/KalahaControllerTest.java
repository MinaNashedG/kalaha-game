package com.kalaha.game.controller;

import com.kalaha.game.KalahaApi;
import com.kalaha.game.model.KalahaGameRequest;
import com.kalaha.game.model.KalahaGameResponse;
import com.kalaha.game.service.KalahaGameService;
import com.kalaha.game.service.KalahaSowService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class KalahaControllerTest {

	@Mock
	private KalahaGameService kalahaGameService;
	@Mock
	private KalahaSowService kalahaSowService;
	@Mock
	private KalahaApi kalahaController;

	@BeforeEach
	public void setup() {
		kalahaController = new KalahaController(kalahaGameService, kalahaSowService);
	}

	@Test
	@DisplayName("Test create game")
	public void testCreateGame() {
		// Given
		KalahaGameRequest kalahaGameRequest = new KalahaGameRequest();
		KalahaGameResponse kalahaGameResponse = new KalahaGameResponse();

		when(kalahaGameService.createNewGame(any())).thenReturn(kalahaGameResponse);

		// When
		KalahaGameResponse result = kalahaController.createGame(kalahaGameRequest);

		// Then
		assertEquals(kalahaGameResponse, result);
	}

	@Test
	@DisplayName("Test sow")
	public void testSow() {
		// Given
		String gameId = "game-id";
		int pitId = 1;
		KalahaGameResponse kalahaGameResponse = new KalahaGameResponse();

		when(kalahaSowService.sow(gameId, pitId)).thenReturn(kalahaGameResponse);

		// When
		KalahaGameResponse result = kalahaController.sow(gameId, pitId);

		// Then
		assertEquals(kalahaGameResponse, result);
	}
}
