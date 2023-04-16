package com.kalaha.game.security;

import com.kalaha.game.dao.KalahaPlayerRepository;
import com.kalaha.game.model.GameUser;
import com.kalaha.game.model.Player;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {
	public static final int BEGIN_INDEX = 7;
	private final UserDetailsServiceImpl userDetailsService;
	private final KalahaPlayerRepository kalahaPlayerRepository;
	private final JwtUtil jwtUtil;

	private final UserContext userContext;

	public JwtRequestFilter(UserDetailsServiceImpl userDetailsService, KalahaPlayerRepository kalahaPlayerRepository,
			JwtUtil jwtUtil,
			UserContext userContext) {
		this.userDetailsService = userDetailsService;
		this.kalahaPlayerRepository = kalahaPlayerRepository;
		this.jwtUtil = jwtUtil;
		this.userContext = userContext;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
			FilterChain chain) throws ServletException, IOException {
		final String authorizationHeader = request.getHeader("Authorization");

		String userId = null;
		String jwt = null;

		if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
			jwt = authorizationHeader.substring(BEGIN_INDEX);
			userId = jwtUtil.getUserIdFromToken(jwt);
		}

		if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
			Player user = kalahaPlayerRepository.findById(userId)
					.orElseThrow(() -> new UsernameNotFoundException("User not found"));

			GameUser gameUserDetails = this.userDetailsService.loadUserByUsername(user.getUserName());

			if (jwtUtil.validateToken(jwt, gameUserDetails.getId())) {
				UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
						new UsernamePasswordAuthenticationToken(
								gameUserDetails, null, gameUserDetails.getAuthorities());
				usernamePasswordAuthenticationToken.setDetails(
						new WebAuthenticationDetailsSource().buildDetails(request));
				SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
				userContext.setUserId(userId);
			}
		}
		chain.doFilter(request, response);
	}
}