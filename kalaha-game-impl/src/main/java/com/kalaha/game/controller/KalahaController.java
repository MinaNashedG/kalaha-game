package com.kalaha.game.controller;

import com.kalaha.game.KalahaApi;
import com.kalaha.game.model.KalahaGameRequest;
import com.kalaha.game.model.KalahaGameResponse;
import com.kalaha.game.service.KalahaGameService;
import com.kalaha.game.service.KalahaSowService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class KalahaController implements KalahaApi {

	private final KalahaGameService kalahaGameService;
	private final KalahaSowService kalahaSowService;

	@Override
	public KalahaGameResponse createGame(@RequestBody KalahaGameRequest kalahaGameRequest) {
		return kalahaGameService.createNewGame(kalahaGameRequest);
	}

	@Override
	public KalahaGameResponse sow(String gameId, Integer pitId) {
		return kalahaSowService.sow(gameId, pitId);
	}

}
