package com.kalaha.game.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "kalaha Game request")
public class KalahaGameRequest implements Serializable {

	private static final long serialVersionUID = 1L;

	@Schema(description = "Number of pits require for each player to initialize the game", example = "6")
	private Integer numberOfPits;

	@Schema(description = "Number of stones require for each pit", example = "6")
	private Integer numberOfStones;

}
