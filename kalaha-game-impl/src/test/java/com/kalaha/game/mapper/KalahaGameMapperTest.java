package com.kalaha.game.mapper;

import com.kalaha.game.model.GameStatus;
import com.kalaha.game.model.KalahaGame;
import com.kalaha.game.model.KalahaGameResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertNull;

class KalahaGameMapperTest {
	private final KalahaGameMapper mapper = Mappers.getMapper(KalahaGameMapper.class);

	@Test
	void should_transform_kalaha_game_response() {
		KalahaGame game = KalahaGame.builder()
				.id("123")
				.board(Arrays.asList(6, 6, 6, 6, 6, 6, 0, 6, 6, 6, 6, 6, 6, 0))
				.status(GameStatus.IN_PROGRESS)
				.numberOfPits(6)
				.numberOfPlayers(2)
				.numberOfStones(6)
				.playerTurn(1)
				.startPit(0)
				.endPit(6)
				.build();
		KalahaGameResponse response = mapper.transform(game);
		Assertions.assertEquals(game.getBoard(), response.getBoard());
		Assertions.assertEquals(game.getStatus().name(), response.getStatus().name());
		Assertions.assertEquals(game.getPlayerTurn(), response.getPlayerTurn());
		Assertions.assertEquals(game.getId(), response.getId());
		Assertions.assertEquals(game.isBonusTurn(), response.isBonusTurn());
	}

	@Test
	void should_return_null_when_transform_kalaha_game_response() {
		assertNull(mapper.transform(null));
	}
}