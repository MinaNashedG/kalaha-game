package com.kalaha.game.security;

import com.kalaha.game.dao.KalahaPlayerRepository;
import com.kalaha.game.exception.AuthenticationFailedException;
import com.kalaha.game.exception.NoUserFoundException;
import com.kalaha.game.exception.PlayerAlreadyExistsException;
import com.kalaha.game.mapper.KalahaPlayerMapper;
import com.kalaha.game.model.AuthenticatedPlayer;
import com.kalaha.game.model.Player;
import com.kalaha.game.model.PlayerDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthenticationServiceTest {

	@Mock
	private AuthenticationManager authenticationManager;

	@Mock
	private JwtUtil jwtUtil;

	@Mock
	private UserDetailsServiceImpl userDetailsService;

	@Mock
	private PasswordEncoder passwordEncoder;

	@Mock
	private KalahaPlayerMapper kalahaPlayerMapper;

	@Mock
	private KalahaPlayerRepository kalahaPlayerRepository;

	private AuthenticationService authenticationService;

	@BeforeEach
	void setUp() {
		authenticationService = new AuthenticationService(authenticationManager, jwtUtil, userDetailsService,
				passwordEncoder, kalahaPlayerMapper, kalahaPlayerRepository);
	}

	@Test
	void should_returnsAuthenticatedPlayer() {
		PlayerDTO playerDTO = new PlayerDTO();
		playerDTO.setUserName("test");
		playerDTO.setPassword("test123");

		UserDetails user = User.builder()
				.username("test")
				.password("test123")
				.roles("USER")
				.build();

		UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
				new UsernamePasswordAuthenticationToken(
						"test",
						"test123");
		when(authenticationManager.authenticate(usernamePasswordAuthenticationToken)).thenReturn(
				null);

		when(userDetailsService.loadUserByUsername(playerDTO.getUserName())).thenReturn(user);
		when(jwtUtil.generateToken(user)).thenReturn("token123");

		AuthenticatedPlayer authenticatedPlayer = authenticationService.authenticate(playerDTO);

		assertEquals("token123", authenticatedPlayer.getToken());
	}

	@Test
	void should_throwsAuthenticationFailedException() {
		PlayerDTO playerDTO = new PlayerDTO();
		playerDTO.setUserName("test");
		playerDTO.setPassword("test123");

		when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
				.thenThrow(new AuthenticationServiceException("Incorrect username or password"));

		assertThrows(AuthenticationFailedException.class, () -> authenticationService.authenticate(playerDTO));
	}

	@Test
	void should_throwsNoUserFoundException() {
		PlayerDTO playerDTO = new PlayerDTO();
		playerDTO.setUserName("test");
		playerDTO.setPassword("test123");

		when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
				.thenThrow(new UsernameNotFoundException("User not found"));

		assertThrows(NoUserFoundException.class, () -> authenticationService.authenticate(playerDTO));
	}

	@Test
	void should_savesPlayerToRepository() {
		PlayerDTO playerDTO = new PlayerDTO();
		playerDTO.setUserName("test");
		playerDTO.setPassword("test123");
		Player expected = Player.builder().build();

		when(kalahaPlayerRepository.findByUserName("test")).thenReturn(null);
		when(passwordEncoder.encode("test123")).thenReturn("hashed_password");

		when(kalahaPlayerMapper.transform(playerDTO)).thenReturn(expected);

		authenticationService.registerUser(playerDTO);

		verify(kalahaPlayerRepository, times(1)).save(expected);
	}

	@Test
	void should_throwsPlayerAlreadyExistsException() {
		PlayerDTO playerDTO = new PlayerDTO();
		playerDTO.setUserName("test");
		playerDTO.setPassword("test123");
		//WHEN
		when(kalahaPlayerRepository.findByUserName("test")).thenReturn(Player.builder().build());

		//THEN
		assertThrows(PlayerAlreadyExistsException.class, () -> authenticationService.registerUser(playerDTO));
	}
}