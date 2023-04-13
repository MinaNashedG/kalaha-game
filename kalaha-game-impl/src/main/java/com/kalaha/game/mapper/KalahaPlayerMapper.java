package com.kalaha.game.mapper;

import com.kalaha.game.model.Player;
import com.kalaha.game.model.PlayerDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface KalahaPlayerMapper {

	Player transform(PlayerDTO player);
}
