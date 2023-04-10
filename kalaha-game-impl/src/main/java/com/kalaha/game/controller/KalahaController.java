package com.kalaha.game.controller;

import com.kalaha.game.KalahaApi;
import com.kalaha.game.model.KalahaGameRequest;
import com.kalaha.game.model.KalahaGameResponse;
import com.kalaha.game.service.KalahaGameService;
import com.kalaha.game.service.KalahaSowService;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class KalahaController implements KalahaApi {

	private final KalahaGameService kalahaGameService;
	private final KalahaSowService kalahaSowService;

	public KalahaController(KalahaGameService kalahaGameService, KalahaSowService kalahaSowService) {
		this.kalahaGameService = kalahaGameService;
		this.kalahaSowService = kalahaSowService;
	}

	@Override
	public KalahaGameResponse createGame(KalahaGameRequest kalahaGameRequest) {
		return kalahaGameService.createNewGame(kalahaGameRequest);
	}

	@Override
	public KalahaGameResponse sow(String gameId, Integer pitId) {
		return kalahaSowService.sow(gameId, pitId);
	}
}
