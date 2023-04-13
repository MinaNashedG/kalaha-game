package com.kalaha.game.security;

import com.kalaha.game.dao.KalahaPlayerRepository;
import com.kalaha.game.model.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserDetailsServiceImplTest {

	public static final String USER_NAME = "test_user";
	public static final String TEST_PASSWORD = "test_password";
	@Mock
	private UserDetailsServiceImpl userDetailsService;

	@Mock
	private KalahaPlayerRepository kalahaPlayerRepository;

	@BeforeEach
	void setUp() {
		userDetailsService = new UserDetailsServiceImpl(kalahaPlayerRepository);
	}

	@Test
	void should_loadUserByUsername_returnsUserDetails() {
		Player player = new Player();
		player.setUserName(USER_NAME);
		player.setPassword(TEST_PASSWORD);
		when(kalahaPlayerRepository.findByUserName(USER_NAME)).thenReturn(player);

		UserDetails userDetails = userDetailsService.loadUserByUsername(USER_NAME);

		assertEquals(USER_NAME, userDetails.getUsername());
		assertEquals(TEST_PASSWORD, userDetails.getPassword());
	}

	@Test
	void should_loadUserByUsername_throwsUsernameNotFoundException() {
		when(kalahaPlayerRepository.findByUserName(USER_NAME)).thenReturn(null);

		assertThrows(UsernameNotFoundException.class, () -> userDetailsService.loadUserByUsername(USER_NAME));
	}

}