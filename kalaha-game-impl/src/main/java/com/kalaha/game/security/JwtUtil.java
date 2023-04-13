package com.kalaha.game.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;

@Component
public class JwtUtil {
	@Value("${jwt.secret}")
	private String secret;

	public String generateToken(UserDetails userDetails) {
		Date now = new Date();
		LocalDateTime expiryDate = LocalDateTime.now().plusMinutes(60);

		return Jwts.builder()
				.setSubject(userDetails.getUsername())
				.setIssuedAt(now)
				.setExpiration(Date.from(expiryDate.toInstant(ZoneOffset.UTC)))
				.signWith(SignatureAlgorithm.HS512, secret)
				.compact();
	}

	public String getUsernameFromToken(String token) {
		Claims claims = Jwts.parser()
				.setSigningKey(secret)
				.parseClaimsJws(token)
				.getBody();

		return claims.getSubject();
	}

	public boolean validateToken(String token, String username) {
		String usernameFromToken = getUsernameFromToken(token);
		return (usernameFromToken.equals(username) && !isTokenExpired(token));
	}

	private boolean isTokenExpired(String token) {
		Claims claims = Jwts.parser()
				.setSigningKey(secret)
				.parseClaimsJws(token)
				.getBody();

		Date expirationDate = claims.getExpiration();
		Date now = new Date();

		return expirationDate.before(now);
	}
}
