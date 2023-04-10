package com.kalaha.game.validator;

import com.kalaha.game.exception.InvalidGameInputException;
import com.kalaha.game.exception.InvalidGameStateException;
import com.kalaha.game.exception.InvalidPlayerTurnException;
import com.kalaha.game.model.GameStatus;
import com.kalaha.game.model.KalahaGame;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.IntStream;

@Component
public class KalahaGameValidator {

	private static final int ZERO = 0;

	public void validateGameAndPit(KalahaGame game, int pitId) {
		List<Integer> gameBoard = game.getBoard();

		if (game.getStatus() == GameStatus.OVER || isGameOver(game)) {
			throw new InvalidGameStateException("Game is already Over.");
		}

		if (pitId >= gameBoard.size() || pitId < ZERO || gameBoard.get(pitId) == ZERO) {
			throw new InvalidGameInputException("Invalid selected pit.");
		}

		if (!isValidPitIdForPlayerTurn(game.getStartPit(), game.getEndPit() - 1, pitId)) {
			throw new InvalidPlayerTurnException("Selected pit id is not belong to current player turn.");
		}

	}

	public boolean isGameOver(KalahaGame game) {
		int startPit = 0;
		int endPit = game.getPitsCount() - 1;
		List<Integer> board = game.getBoard();
		while (endPit < board.size()) {
			if (isEmptyPits(startPit, endPit, board)) {
				return true;
			}
			startPit += game.getPitsCount() + 1;
			endPit = startPit + game.getPitsCount() - 1;
		}
		return false;
	}

	private boolean isEmptyPits(int startPit, int endPit, List<Integer> board) {
		return IntStream.range(startPit, endPit)
				.allMatch(index -> board.get(index) == ZERO);
	}

	private boolean isValidPitIdForPlayerTurn(int begin, int end, int pitId) {
		return pitId >= begin && pitId <= end;
	}

}
