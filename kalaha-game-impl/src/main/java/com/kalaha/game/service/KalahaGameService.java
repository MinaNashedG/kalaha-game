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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class KalahaGameService {

	private final KalahaGameRepository kalahaGameRepository;
	private final KalahaGameMapper kalahaGameMapper;
	private final KalahaGameConfig kalahaGameConfig;

	private final KalahaGameValidator kalahaGameValidator;

	private final KalahaPlayerRepository kalahaPlayerRepository;

	private final UserContext userContext;

	public KalahaGameService(KalahaGameRepository kalahaGameRepository, KalahaGameMapper kalahaGameMapper,
			KalahaGameConfig kalahaGameConfig, KalahaGameValidator kalahaGameValidator,
			KalahaPlayerRepository kalahaPlayerRepository, UserContext userContext) {
		this.kalahaGameRepository = kalahaGameRepository;
		this.kalahaGameMapper = kalahaGameMapper;
		this.kalahaGameConfig = kalahaGameConfig;
		this.kalahaGameValidator = kalahaGameValidator;
		this.kalahaPlayerRepository = kalahaPlayerRepository;
		this.userContext = userContext;
	}

	public KalahaGameResponse createNewGame(KalahaGameRequest kalahaGameRequest) {

		KalahaGame game = kalahaGameRepository.save(createGameInstance(kalahaGameRequest));
		return kalahaGameMapper.transform(game);
	}

	private KalahaGame createGameInstance(KalahaGameRequest kalahaGameRequest) {

		kalahaGameValidator.validateGameRequest(Optional.ofNullable(kalahaGameRequest)
				.orElseThrow(() -> new InvalidGameInputException("Request can't be null")));

		Player opponent = kalahaPlayerRepository.findById(kalahaGameRequest.getOpponent())
				.orElseThrow(() -> new InvalidGameInputException("Opponent is not exist."));

		Player player = kalahaPlayerRepository.findById(userContext.getUserId())
				.orElseThrow(() -> new InvalidGameInputException("User Context is invalid"));

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
				.numberOfPits(numberOfPits)
				.numberOfPlayers(kalahaGameConfig.getDefaultPlayers())
				.numberOfStones(numberOfStones)
				.playerTurn(userContext.getUserId())
				.playerTurnIndex(0)
				.players(Arrays.asList(player, opponent))
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

	private Integer getNumberOfPlayerPits(KalahaGameRequest kalahaGameRequest) {
		return Optional.ofNullable(kalahaGameRequest)
				.map(KalahaGameRequest::getNumberOfPits)
				.orElse(kalahaGameConfig.getDefaultPits());
	}

	private Integer getNumberOfTotalPits(KalahaGameRequest kalahaGameRequest) {
		final Integer numberOfPlayers = kalahaGameConfig.getDefaultPlayers();
		return Optional.ofNullable(kalahaGameRequest)
				.map(KalahaGameRequest::getNumberOfPits)
				.map(pits -> calculateTotalPits(pits, numberOfPlayers))
				.orElse(calculateTotalPits(kalahaGameConfig.getDefaultPits(), numberOfPlayers));
	}

	private int calculateTotalPits(int pits, int numberOfPlayers) {
		return (pits * numberOfPlayers) + numberOfPlayers;
	}

}
