package com.kalaha.game.dao;

import com.kalaha.game.model.KalahaGame;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KalahaGameRepository extends MongoRepository<KalahaGame, String> {
}