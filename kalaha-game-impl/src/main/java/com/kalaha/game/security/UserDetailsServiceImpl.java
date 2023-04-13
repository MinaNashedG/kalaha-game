package com.kalaha.game.security;

import com.kalaha.game.dao.KalahaPlayerRepository;
import com.kalaha.game.model.Player;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class UserDetailsServiceImpl implements UserDetailsService {

	private final KalahaPlayerRepository kalahaPlayerRepository;

	public UserDetailsServiceImpl(KalahaPlayerRepository kalahaPlayerRepository) {
		this.kalahaPlayerRepository = kalahaPlayerRepository;
	}

	@Override
	public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
		Player player = kalahaPlayerRepository.findByUserName(userName);
		if (player == null) {
			throw new UsernameNotFoundException("User not found");
		}
		return User.builder()
				.username(player.getUserName())
				.password(player.getPassword())
				.roles("USER")
				.build();
	}
}
