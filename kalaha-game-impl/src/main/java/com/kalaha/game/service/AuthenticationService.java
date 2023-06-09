package com.kalaha.game.service;

import com.kalaha.game.dao.KalahaPlayerRepository;
import com.kalaha.game.exception.AuthenticationFailedException;
import com.kalaha.game.exception.InvalidGameInputException;
import com.kalaha.game.exception.NoUserFoundException;
import com.kalaha.game.exception.PlayerAlreadyExistsException;
import com.kalaha.game.mapper.KalahaPlayerMapper;
import com.kalaha.game.model.AuthenticatedPlayer;
import com.kalaha.game.model.Player;
import com.kalaha.game.model.PlayerDTO;
import com.kalaha.game.security.JwtUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class AuthenticationService {
	private final AuthenticationManager authenticationManager;
	private final JwtUtil jwtUtil;
	private final PasswordEncoder passwordEncoder;
	private final KalahaPlayerMapper kalahaPlayerMapper;
	private final KalahaPlayerRepository kalahaPlayerRepository;

	public AuthenticatedPlayer authenticate(PlayerDTO playerDTO) {

		if (playerDTO == null || StringUtils.isBlank(playerDTO.getUserName()) || StringUtils.isBlank(
				playerDTO.getPassword())) {
			throw new InvalidGameInputException("Username or password can't be empty");
		}
		authenticateUser(playerDTO);
		final Player gameUserDetails = kalahaPlayerRepository.findByUserName(playerDTO.getUserName());
		final String jwt = jwtUtil.generateToken(gameUserDetails.getId());

		return new AuthenticatedPlayer(jwt, gameUserDetails.getId());
	}

	private void authenticateUser(PlayerDTO playerDTO) {
		try {
			authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(playerDTO.getUserName(),
							playerDTO.getPassword()));
		}
		catch (UsernameNotFoundException ex) {
			log.error("Invalid username or password", ex);
			throw new NoUserFoundException("User not found");
		}
		catch (AuthenticationException ex) {
			log.error("fail to authenticate user", ex);
			throw new AuthenticationFailedException("Incorrect username or password");
		}
	}

	public void registerUser(PlayerDTO playerDTO) {
		Player player = kalahaPlayerRepository.findByUserName(playerDTO.getUserName());
		if (player != null) {
			throw new PlayerAlreadyExistsException("User already exists");
		}

		playerDTO.setPassword(passwordEncoder.encode(playerDTO.getPassword()));

		kalahaPlayerRepository.save(kalahaPlayerMapper.transform(playerDTO));
	}

}
