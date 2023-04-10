package com.kalaha.game.service;

import com.kalaha.game.dao.KalahaGameRepository;
import com.kalaha.game.exception.NoGameFoundException;
import com.kalaha.game.mapper.KalahaGameMapper;
import com.kalaha.game.model.GameStatus;
import com.kalaha.game.model.KalahaGame;
import com.kalaha.game.model.KalahaGameResponse;
import com.kalaha.game.validator.KalahaGameValidator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
public class KalahaSowService {
	private final KalahaGameRepository kalahaGameRepository;
	private final KalahaGameMapper kalahaGameMapper;
	private final KalahaGameValidator kalahaGameValidator;

	public KalahaSowService(KalahaGameRepository kalahaGameRepository, KalahaGameMapper kalahaGameMapper,
			KalahaGameValidator kalahaGameValidator) {
		this.kalahaGameRepository = kalahaGameRepository;
		this.kalahaGameMapper = kalahaGameMapper;
		this.kalahaGameValidator = kalahaGameValidator;
	}

	@Transactional
	public KalahaGameResponse sow(String gameId, Integer pitId) {
		KalahaGame game = kalahaGameRepository.findById(gameId)
				.orElseThrow(() -> new NoGameFoundException(String.format("Game with id %s not found.", gameId)));

		kalahaGameValidator.validateGameAndPit(game, pitId);
		sowStones(pitId, game);

		if (kalahaGameValidator.isGameOver(game)) {
			game.setStatus(GameStatus.OVER);
		} else {
			game.setStatus(GameStatus.IN_PROGRESS);
		}

		if (!game.isBonusTurn()) {
			updateGamePlayerTurn(game);
		}

		return kalahaGameMapper.transform(kalahaGameRepository.save(game));
	}

	private KalahaGame updateGamePlayerTurn(KalahaGame game) {
		game.setPlayerTurn(Objects.equals(game.getPlayersCount(), game.getPlayerTurn()) ? 1
				: game.getPlayerTurn() + 1);
		game.setStartPit(game.getPlayerTurn() == 1 ? 0 : game.getStartPit() + game.getPitsCount() + 1);
		game.setEndPit(game.getStartPit() + game.getPitsCount());
		return game;
	}

	private void sowStones(Integer pitId, KalahaGame game) {
		List<Integer> gameBoard = game.getBoard();
		int stones = gameBoard.get(pitId);
		gameBoard.set(pitId, 0);
		int pitIndex = pitId + 1;
		while (stones > 0) {
			pitIndex = pitIndex % gameBoard.size();
			if (stones == 1 && gameBoard.get(pitIndex) == 0
					&& pitIndex <= game.getEndPit() - 1 && pitIndex >= game.getStartPit()) {
				//capture case
				int oppositePit = (game.getPitsCount() * game.getPlayersCount()) - pitIndex;
				int oppositeStones = gameBoard.get(oppositePit);
				gameBoard.set(game.getEndPit(), gameBoard.get(pitIndex) + oppositeStones);
				gameBoard.set(oppositePit, 0);
				break;
			} else {
				game.setBonusTurn(stones == 1 && pitIndex == game.getEndPit());
			}

			gameBoard.set(pitIndex, gameBoard.get(pitIndex) + 1);
			stones--;
			pitIndex++;
		}
	}

}
