package com.kalaha.game.controller;

import com.kalaha.game.model.AuthenticatedPlayer;
import com.kalaha.game.model.PlayerDTO;
import com.kalaha.game.security.AuthenticationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class AuthenticationControllerTest {

	public static final String ID = "1";
	@Mock
	private AuthenticationService authenticationService;

	@Mock
	private AuthenticationController authenticationController;

	@BeforeEach
	void setUp() {
		authenticationController = new AuthenticationController(authenticationService);
	}

	@Test
	void should_returnAuthenticatePlayer() {
		// given
		PlayerDTO playerDTO = new PlayerDTO(ID, "username", "password", "email@email.com");
		AuthenticatedPlayer authenticatedPlayer = new AuthenticatedPlayer("token", ID);
		Mockito.when(authenticationService.authenticate(playerDTO)).thenReturn(authenticatedPlayer);

		// when
		AuthenticatedPlayer response = authenticationController.login(playerDTO);

		// then
		assertEquals(response.getToken(), "token");
	}

	@Test
	void should_registerPlayer() {
		// given
		PlayerDTO playerDTO = new PlayerDTO(ID, "username", "password", "email@email.com");

		// when
		authenticationController.register(playerDTO);

		// then
		Mockito.verify(authenticationService).registerUser(playerDTO);
	}
}