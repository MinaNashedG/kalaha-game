package com.kalaha.game.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "kalaha_games")
public class KalahaGame {

	@Id
	private String id;

	private List<Integer> board;

	private Integer playerTurn;

	private GameStatus status;

	private Integer numberOfPits;

	private Integer numberOfPlayers;

	private Integer numberOfStones;

	private boolean bonusTurn;

	private Integer startPit;

	private Integer endPit;

	private List<Player> playerDTOS;

	private Integer playerWin;
}
