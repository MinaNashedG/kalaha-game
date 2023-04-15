package com.kalaha.game.security;

import com.kalaha.game.dao.KalahaPlayerRepository;
import com.kalaha.game.model.GameUser;
import com.kalaha.game.model.Player;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Optional;

@Component
public class UserDetailsServiceImpl implements UserDetailsService {

	private final KalahaPlayerRepository kalahaPlayerRepository;

	public UserDetailsServiceImpl(KalahaPlayerRepository kalahaPlayerRepository) {
		this.kalahaPlayerRepository = kalahaPlayerRepository;
	}

	@Override
	public GameUser loadUserByUsername(String userName) throws UsernameNotFoundException {
		Player player = Optional.ofNullable(kalahaPlayerRepository.findByUserName(userName))
				.orElseThrow(() -> new UsernameNotFoundException("User not found"));

		return new GameUser(player.getUserName(),
				player.getPassword(),
				Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")),
				player.getId());
	}
}
