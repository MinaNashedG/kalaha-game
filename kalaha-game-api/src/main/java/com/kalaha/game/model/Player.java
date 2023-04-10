package com.kalaha.game.model;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
class Player {

	private Integer id;
	private String name;
}
