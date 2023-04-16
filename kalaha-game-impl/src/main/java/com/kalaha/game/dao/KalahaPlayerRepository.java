package com.kalaha.game.dao;

import com.kalaha.game.model.Player;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KalahaPlayerRepository extends MongoRepository<Player, String> {
	Player findByUserName(String userName);

}