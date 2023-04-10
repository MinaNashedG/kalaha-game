package com.kalaha.game.service;

import com.kalaha.game.config.KalahaGameConfig;
import com.kalaha.game.dao.KalahaGameRepository;
import com.kalaha.game.mapper.KalahaGameMapper;
import com.kalaha.game.model.GameStatus;
import com.kalaha.game.model.KalahaGame;
import com.kalaha.game.model.KalahaGameRequest;
import com.kalaha.game.model.KalahaGameResponse;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class KalahaGameService {

	private static final int PLAYER_TURN = 1;
	private final KalahaGameRepository kalahaGameRepository;
	private final KalahaGameMapper kalahaGameMapper;
	private final KalahaGameConfig kalahaGameConfig;

	public KalahaGameService(KalahaGameRepository kalahaGameRepository, KalahaGameMapper kalahaGameMapper,
			KalahaGameConfig kalahaGameConfig) {
		this.kalahaGameRepository = kalahaGameRepository;
		this.kalahaGameMapper = kalahaGameMapper;
		this.kalahaGameConfig = kalahaGameConfig;
	}

	public KalahaGameResponse createNewGame(KalahaGameRequest kalahaGameRequest) {
		KalahaGame game = kalahaGameRepository.save(createGameInstance(kalahaGameRequest));
		return kalahaGameMapper.transform(game);
	}

	private KalahaGame createGameInstance(KalahaGameRequest kalahaGameRequest) {
		Integer numberOfPits = getNumberOfPlayerPits(kalahaGameRequest);
		Integer numberOfTotalPits = getNumberOfTotalPits(kalahaGameRequest);
		int[] pits = new int[numberOfTotalPits];
		Integer numberOfStones = getNumberOfStones(kalahaGameRequest);
		Arrays.fill(pits, numberOfStones);
		List<Integer> pitsList = Arrays.stream(pits).boxed().collect(Collectors.toList());
		resetPlayerScorePits(numberOfPits, numberOfTotalPits, pitsList);
		return KalahaGame.builder()
				.board(pitsList)
				.status(GameStatus.NEW)
				.pitsCount(numberOfPits)
				.playersCount(getNumberOfPlayers(kalahaGameRequest))
				.stonesCount(numberOfStones)
				.playerTurn(PLAYER_TURN)
				.startPit(0)
				.endPit(numberOfPits)
				.build();
	}

	private void resetPlayerScorePits(Integer numberOfPits, Integer numberOfTotalPits, List<Integer> pits) {
		int scorePitPlayer = numberOfPits;
		while (scorePitPlayer < numberOfTotalPits) {
			pits.set(scorePitPlayer, 0);
			scorePitPlayer += numberOfPits + 1;
		}
	}

	private Integer getNumberOfStones(KalahaGameRequest kalahaGameRequest) {
		return Optional.ofNullable(kalahaGameRequest)
				.map(KalahaGameRequest::getNumberOfStones)
				.orElse(kalahaGameConfig.getDefaultStones());
	}

	private Integer getNumberOfPlayers(KalahaGameRequest kalahaGameRequest) {
		return Optional.ofNullable(kalahaGameRequest)
				.map(KalahaGameRequest::getNumberOfPlayers)
				.orElse(kalahaGameConfig.getDefaultPlayers());
	}

	private Integer getNumberOfPlayerPits(KalahaGameRequest kalahaGameRequest) {
		return Optional.ofNullable(kalahaGameRequest)
				.map(KalahaGameRequest::getNumberOfPits)
				.orElse(kalahaGameConfig.getDefaultPits());
	}

	private Integer getNumberOfTotalPits(KalahaGameRequest kalahaGameRequest) {
		final Integer numberOfPlayers = getNumberOfPlayers(kalahaGameRequest);
		return Optional.ofNullable(kalahaGameRequest)
				.map(KalahaGameRequest::getNumberOfPits)
				.filter(Objects::nonNull)
				.map(pits -> calculateTotalPits(pits, numberOfPlayers))
				.orElse(calculateTotalPits(kalahaGameConfig.getDefaultPits(), numberOfPlayers));
	}

	private int calculateTotalPits(int pits, int numberOfPlayers) {
		return (pits * numberOfPlayers) + numberOfPlayers;
	}

}
