package com.kalaha.game.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;

@Data
@Component
@ConfigurationProperties(prefix = "kalaha.game")
public class KalahaGameConfig {

	@NotNull
	private Integer defaultPlayers;

	@NotNull
	private Integer defaultPits;

	@NotNull
	private Integer defaultStones;
}
