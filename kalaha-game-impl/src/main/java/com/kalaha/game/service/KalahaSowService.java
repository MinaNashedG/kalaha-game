package com.kalaha.game.service;

import com.kalaha.game.dao.KalahaGameRepository;
import com.kalaha.game.exception.NoGameFoundException;
import com.kalaha.game.mapper.KalahaGameMapper;
import com.kalaha.game.model.GameStatus;
import com.kalaha.game.model.KalahaGame;
import com.kalaha.game.model.KalahaGameResponse;
import com.kalaha.game.model.Player;
import com.kalaha.game.security.UserContext;
import com.kalaha.game.validator.KalahaGameValidator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class KalahaSowService {
	private final KalahaGameRepository kalahaGameRepository;
	private final KalahaGameMapper kalahaGameMapper;
	private final KalahaGameValidator kalahaGameValidator;

	private final UserContext userContext;

	public KalahaSowService(KalahaGameRepository kalahaGameRepository, KalahaGameMapper kalahaGameMapper,
			KalahaGameValidator kalahaGameValidator, UserContext userContext) {
		this.kalahaGameRepository = kalahaGameRepository;
		this.kalahaGameMapper = kalahaGameMapper;
		this.kalahaGameValidator = kalahaGameValidator;
		this.userContext = userContext;
	}

	public KalahaGameResponse sow(String gameId, Integer pitId) {
		KalahaGame game = kalahaGameRepository.findById(gameId)
				.orElseThrow(() -> new NoGameFoundException(String.format("Game with id %s not found.", gameId)));

		kalahaGameValidator.validateGame(game, pitId, userContext.getUserId());
		sowStones(pitId, game);
		checkGameOver(game);
		return kalahaGameMapper.transform(kalahaGameRepository.save(game));
	}

	private void checkGameOver(KalahaGame game) {
		if (kalahaGameValidator.isGameOver(game)) {
			game.setStatus(GameStatus.OVER);
			game.setWinner(playerWinner(game).getId());
		} else {
			game.setStatus(GameStatus.IN_PROGRESS);
			if (!game.isBonusTurn()) {
				updateGamePlayerTurn(game);
			}
		}
	}

	private Player playerWinner(KalahaGame game) {
		int playerIndex = 0;
		int maxScore = 0;
		final Integer numberOfPits = game.getNumberOfPits();
		int scorePitPlayer = numberOfPits;
		while (scorePitPlayer < game.getBoard().size()) {
			if (game.getBoard().get(scorePitPlayer) > maxScore) {
				playerIndex++;
				maxScore = game.getBoard().get(scorePitPlayer);
			}
			scorePitPlayer += numberOfPits + 1;
		}
		return game.getPlayers().get(playerIndex - 1);
	}

	private void updateGamePlayerTurn(KalahaGame game) {
		game.setPlayerTurnIndex(game.getNumberOfPlayers() == game.getPlayerTurnIndex() + 1 ?
				0 : game.getPlayerTurnIndex() + 1);
		game.setPlayerTurn(game.getPlayers().get(game.getPlayerTurnIndex()).getId());
		game.setStartPit(game.getPlayerTurnIndex() == 0 ? 0 : game.getStartPit() + game.getNumberOfPits() + 1);
		game.setEndPit(game.getStartPit() + game.getNumberOfPits());
	}

	private void sowStones(Integer pitId, KalahaGame game) {
		List<Integer> gameBoard = game.getBoard();
		int stones = gameBoard.get(pitId);
		gameBoard.set(pitId, 0);
		int pitIndex = pitId + 1;
		while (stones > 0) {
			pitIndex = pitIndex % gameBoard.size();
			if (isCapturePit(game, gameBoard, stones, pitIndex)) break;
			gameBoard.set(pitIndex, gameBoard.get(pitIndex) + 1);
			stones--;
			pitIndex++;
		}
	}

	private boolean isCapturePit(KalahaGame game, List<Integer> gameBoard, int stones, int pitIndex) {
		if (isLastStoneAndEmptyOwnPit(game, gameBoard, stones, pitIndex)) {
			captureStones(game, gameBoard, pitIndex);
			return true;
		} else {
			game.setBonusTurn(stones == 1 && pitIndex == game.getEndPit());
		}
		return false;
	}

	private void captureStones(KalahaGame game, List<Integer> gameBoard, int pitIndex) {
		int oppositePit = (game.getNumberOfPits() * game.getNumberOfPlayers()) - pitIndex;
		int oppositeStones = gameBoard.get(oppositePit);
		if (oppositeStones > 0) {
			int playerPit = gameBoard.get(game.getEndPit());
			gameBoard.set(game.getEndPit(), playerPit + oppositeStones + 1);
			gameBoard.set(oppositePit, 0);
		} else {
			gameBoard.set(pitIndex, 1);
		}
	}

	private boolean isLastStoneAndEmptyOwnPit(KalahaGame game, List<Integer> gameBoard, int stones, int pitIndex) {
		return stones == 1 && gameBoard.get(pitIndex) == 0
				&& pitIndex <= game.getEndPit() - 1
				&& pitIndex >= game.getStartPit();
	}

}
