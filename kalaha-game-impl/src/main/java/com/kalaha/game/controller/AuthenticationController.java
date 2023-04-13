package com.kalaha.game.controller;

import com.kalaha.game.AuthenticationApi;
import com.kalaha.game.model.AuthenticatedPlayer;
import com.kalaha.game.model.PlayerDTO;
import com.kalaha.game.security.AuthenticationService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthenticationController implements AuthenticationApi {

	private final AuthenticationService authenticationService;

	public AuthenticationController(AuthenticationService authenticationService) {
		this.authenticationService = authenticationService;
	}

	@Override
	public AuthenticatedPlayer login(@RequestBody PlayerDTO playerDTO) {
		return authenticationService.authenticate(playerDTO);
	}

	@Override
	public void register(@RequestBody PlayerDTO playerDTO) {
		authenticationService.registerUser(playerDTO);
	}
}
