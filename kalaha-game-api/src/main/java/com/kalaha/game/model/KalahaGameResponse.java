package com.kalaha.game.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Kalaha Game response model")
public class KalahaGameResponse {

	@Schema(description = "Game Unique id", example = "e58ed763fdbaaadc15f3")
	private String id;

	@Schema(description = "The Game board which include number of stones in each pit.",
			example = "[6,1,0,2,6,4,3,2,3,4,2,3,4,8]")
	private List<Integer> board;

	@Schema(description = "Player turn number.", example = "1")
	private Integer playerTurn;

	@Schema(description = "Status of the game.", example = "IN_PROGRESS")
	private Status status;

	@Schema(description = "Boolean flag indicate if the player has to play another round.", example = "true")
	private boolean bonusTurn;

	@Schema(description = "Indicate the player who won the game.", example = "1")
	private Integer playerWin;
}
