package com.kalaha.game.security;

import com.kalaha.game.dao.KalahaPlayerRepository;
import com.kalaha.game.exception.AuthenticationFailedException;
import com.kalaha.game.exception.InvalidGameInputException;
import com.kalaha.game.exception.NoUserFoundException;
import com.kalaha.game.exception.PlayerAlreadyExistsException;
import com.kalaha.game.mapper.KalahaPlayerMapper;
import com.kalaha.game.model.AuthenticatedPlayer;
import com.kalaha.game.model.Player;
import com.kalaha.game.model.PlayerDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class AuthenticationService {
	private final AuthenticationManager authenticationManager;
	private final JwtUtil jwtUtil;
	private final UserDetailsServiceImpl userDetailsService;
	private final PasswordEncoder passwordEncoder;

	private final KalahaPlayerMapper kalahaPlayerMapper;

	private final KalahaPlayerRepository kalahaPlayerRepository;

	public AuthenticationService(AuthenticationManager authenticationManager, JwtUtil jwtUtil,
			UserDetailsServiceImpl userDetailsService, PasswordEncoder passwordEncoder,
			KalahaPlayerMapper kalahaPlayerMapper, KalahaPlayerRepository kalahaPlayerRepository) {
		this.authenticationManager = authenticationManager;
		this.jwtUtil = jwtUtil;
		this.userDetailsService = userDetailsService;
		this.passwordEncoder = passwordEncoder;
		this.kalahaPlayerMapper = kalahaPlayerMapper;
		this.kalahaPlayerRepository = kalahaPlayerRepository;
	}

	public AuthenticatedPlayer authenticate(PlayerDTO playerDTO) {
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
		final UserDetails userDetails = userDetailsService.loadUserByUsername(playerDTO.getUserName());

		final String jwt = jwtUtil.generateToken(userDetails);

		return new AuthenticatedPlayer(jwt);
	}

	public void registerUser(PlayerDTO playerDTO) {
		Player player = kalahaPlayerRepository.findByUserName(playerDTO.getUserName());
		if (player != null) {
			throw new PlayerAlreadyExistsException("User already exists");
		}

		playerDTO.setPassword(passwordEncoder.encode(Optional.of(playerDTO.getPassword())
				.filter(StringUtils::isNotBlank)
				.orElseThrow(() -> new InvalidGameInputException("Password can't be null or empty"))));

		kalahaPlayerRepository.save(kalahaPlayerMapper.transform(playerDTO));
	}

}
