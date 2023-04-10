package com.kalaha.game.mapper;

import com.kalaha.game.model.KalahaGame;
import com.kalaha.game.model.KalahaGameResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface KalahaGameMapper {

	KalahaGameResponse transform(KalahaGame game);
}
