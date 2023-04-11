package com.kalaha.game.validator;

import com.kalaha.game.exception.InvalidGameInputException;
import com.kalaha.game.exception.InvalidGameStateException;
import com.kalaha.game.exception.InvalidPlayerTurnException;
import com.kalaha.game.model.GameStatus;
import com.kalaha.game.model.KalahaGame;
import com.kalaha.game.model.KalahaGameRequest;
import lombok.extern.slf4j.Slf4j;
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

	public void validateGameAndPit(KalahaGame game, int pitId) {
		List<Integer> gameBoard = game.getBoard();

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

	public void validateGameRequest(KalahaGameRequest kalahaGameRequest) {

		final boolean requestNotEmpty = kalahaGameRequest != null && kalahaGameRequest.getNumberOfPits() != null;
		if (requestNotEmpty && (kalahaGameRequest.getNumberOfPits() < MIN_PITS
				|| kalahaGameRequest.getNumberOfPits() > MAX_PITS)) {

			throw new InvalidGameInputException("Invalid Number of Pits");
		}

		final boolean stoneNotEmpty = kalahaGameRequest != null && kalahaGameRequest.getNumberOfStones() != null;
		if (stoneNotEmpty && (kalahaGameRequest.getNumberOfStones() < MIN_STONES
				|| kalahaGameRequest.getNumberOfStones() > MAX_STONES)) {

			throw new InvalidGameInputException("Invalid Number of Stones");
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
