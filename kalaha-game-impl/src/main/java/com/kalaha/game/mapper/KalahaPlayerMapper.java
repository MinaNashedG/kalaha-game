package com.kalaha.game.mapper;

import com.kalaha.game.model.Player;
import com.kalaha.game.model.PlayerDTO;
import com.kalaha.game.model.PlayerResponse;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface KalahaPlayerMapper {
	List<Player> transform(List<PlayerDTO> players);

	Player transform(PlayerDTO player);

	PlayerResponse transform(Player player);
}
