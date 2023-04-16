package com.kalaha.game.mapper;

import com.kalaha.game.model.Player;
import com.kalaha.game.model.PlayerDTO;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

class KalahaPlayerMapperTest {
	private final KalahaPlayerMapper mapper = Mappers.getMapper(KalahaPlayerMapper.class);

	@Test
	public void should_return_player() {
		PlayerDTO playerDTO = new PlayerDTO();
		playerDTO.setUserName("testUser");
		playerDTO.setPassword("testPassword");

		Player player = mapper.transform(playerDTO);

		assertEquals(player.getUserName(), playerDTO.getUserName());
		assertEquals(player.getPassword(), playerDTO.getPassword());
	}

	@Test
	public void should_not_throw_exception_in_case_of_null() {

		assertDoesNotThrow(() -> mapper.transform(Collections.singletonList(null)));
		assertDoesNotThrow(() -> mapper.transform(PlayerDTO.builder().build()));
	}
}