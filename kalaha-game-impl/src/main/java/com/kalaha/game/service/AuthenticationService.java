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
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AuthenticationService {
	private final AuthenticationManager authenticationManager;
	private final JwtUtil jwtUtil;
	private final PasswordEncoder passwordEncoder;

	private final KalahaPlayerMapper kalahaPlayerMapper;

	private final KalahaPlayerRepository kalahaPlayerRepository;

	public AuthenticationService(AuthenticationManager authenticationManager, JwtUtil jwtUtil,
			PasswordEncoder passwordEncoder, KalahaPlayerMapper kalahaPlayerMapper,
			KalahaPlayerRepository kalahaPlayerRepository) {
		this.authenticationManager = authenticationManager;
		this.jwtUtil = jwtUtil;
		this.passwordEncoder = passwordEncoder;
		this.kalahaPlayerMapper = kalahaPlayerMapper;
		this.kalahaPlayerRepository = kalahaPlayerRepository;
	}

	public AuthenticatedPlayer authenticate(PlayerDTO playerDTO) {

		if (playerDTO == null || playerDTO.getUserName() == null || playerDTO.getPassword() == null) {
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
			log.error(ex.getMessage());
			throw new NoUserFoundException("User not found");
		}
		catch (AuthenticationException ex) {
			log.error(ex.getMessage());
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
