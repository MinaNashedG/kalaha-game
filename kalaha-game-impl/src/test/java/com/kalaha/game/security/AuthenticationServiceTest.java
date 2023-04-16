package com.kalaha.game.security;

import com.kalaha.game.dao.KalahaPlayerRepository;
import com.kalaha.game.exception.AuthenticationFailedException;
import com.kalaha.game.exception.InvalidGameInputException;
import com.kalaha.game.exception.NoUserFoundException;
import com.kalaha.game.exception.PlayerAlreadyExistsException;
import com.kalaha.game.mapper.KalahaPlayerMapper;
import com.kalaha.game.model.AuthenticatedPlayer;
import com.kalaha.game.model.GameUser;
import com.kalaha.game.model.Player;
import com.kalaha.game.model.PlayerDTO;
import com.kalaha.game.service.AuthenticationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.lenient;
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
		authenticationService = new AuthenticationService(authenticationManager, jwtUtil,
				passwordEncoder, kalahaPlayerMapper, kalahaPlayerRepository);
		lenient().when(kalahaPlayerRepository.findByUserName("test")).thenReturn(Player.builder()
				.id("123")
				.userName("test")
				.password("test123").build());
	}

	@Test
	void should_returnsAuthenticatedPlayer() {
		PlayerDTO playerDTO = new PlayerDTO();
		playerDTO.setUserName("test");
		playerDTO.setPassword("test123");

		GameUser gameUser = new GameUser(
				"test",
				"test123",
				Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")),
				"123");

		UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
				new UsernamePasswordAuthenticationToken(
						"test",
						"test123");
		when(authenticationManager.authenticate(usernamePasswordAuthenticationToken)).thenReturn(
				null);

		when(jwtUtil.generateToken(gameUser.getId())).thenReturn("token123");

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
	void should_throwsNoInvalidInputGameException() {
		PlayerDTO playerDTO = new PlayerDTO();

		assertThrows(InvalidGameInputException.class, () -> authenticationService.authenticate(playerDTO));
		assertThrows(InvalidGameInputException.class, () -> authenticationService.authenticate(null));
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