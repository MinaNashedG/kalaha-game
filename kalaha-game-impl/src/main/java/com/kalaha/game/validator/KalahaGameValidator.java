package com.kalaha.game.validator;

import com.kalaha.game.exception.InvalidGameInputException;
import com.kalaha.game.exception.InvalidGameStateException;
import com.kalaha.game.exception.InvalidPlayerTurnException;
import com.kalaha.game.model.GameStatus;
import com.kalaha.game.model.KalahaGame;
import com.kalaha.game.model.KalahaGameRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.IntStream;

@Component
@Slf4j
public class KalahaGameValidator {

	private static final int ZERO = 0;
	public static final int MIN_PITS = 4;
	public static final int MAX_PITS = 10;
	public static final int MIN_STONES = 3;
	public static final int MAX_STONES = 10;

	public void validateGame(KalahaGame game, int pitId, String playerId) {
		List<Integer> gameBoard = game.getBoard();
		final String playerTurnId = getPlayerTurnId(game);
		if (playerId == null || !StringUtils.equals(playerId, playerTurnId)) {
			throw new InvalidPlayerTurnException("Invalid Player turn.");
		}

		if (game.getStatus() == GameStatus.OVER || isGameOver(game)) {
			throw new InvalidGameStateException("Game is already Over.");
		}

		if (pitId >= gameBoard.size() || pitId < ZERO || gameBoard.get(pitId) == ZERO) {
			throw new InvalidGameInputException("Invalid selected pit id or number of stones inside pit is zero.");
		}

		if (pitId == game.getEndPit()) {
			throw new InvalidGameInputException("Invalid pit , the score pit can't be selected.");
		}

		if (!isValidPitIdForPlayerTurn(game.getStartPit(), game.getEndPit() - 1, pitId)) {
			throw new InvalidPlayerTurnException("pit id is not belong to current player turn .");
		}

	}

	private String getPlayerTurnId(KalahaGame game) {
		if (game.getPlayerTurn() == null || CollectionUtils.isEmpty(game.getPlayers())) {
			throw new InvalidGameStateException("Invalid Game Players.");
		}
		return game.getPlayers().get(game.getPlayerTurnIndex()).getId();
	}

	public void validateGameRequest(KalahaGameRequest kalahaGameRequest) {

		final Integer numberOfPits = kalahaGameRequest.getNumberOfPits();
		if (numberOfPits != null && (numberOfPits < MIN_PITS || numberOfPits > MAX_PITS)) {
			throw new InvalidGameInputException("Invalid Number of Pits");
		}

		final Integer numberOfStones = kalahaGameRequest.getNumberOfStones();
		if (numberOfStones != null && (numberOfStones < MIN_STONES || numberOfStones > MAX_STONES)) {
			throw new InvalidGameInputException("Invalid Number of Stones");
		}

		if (StringUtils.isBlank(kalahaGameRequest.getOpponent())) {
			throw new InvalidGameInputException("Opponent can't be null or empty");
		}

	}

	public boolean isGameOver(KalahaGame game) {
		int startPit = 0;
		int endPit = game.getNumberOfPits() - 1;
		List<Integer> board = game.getBoard();
		while (endPit < board.size()) {
			if (isEmptyPits(startPit, endPit, board)) {
				return true;
			}
			startPit += game.getNumberOfPits() + 1;
			endPit = startPit + game.getNumberOfPits() - 1;
		}
		return false;
	}

	private boolean isEmptyPits(int startPit, int endPit, List<Integer> board) {
		return IntStream.rangeClosed(startPit, endPit)
				.allMatch(index -> board.get(index) == ZERO);
	}

	private boolean isValidPitIdForPlayerTurn(int begin, int end, int pitId) {
		return pitId >= begin && pitId <= end;
	}

}
