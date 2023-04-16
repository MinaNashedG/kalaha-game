package com.kalaha.game.model;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

public class GameUser extends User {

	@Getter
	private String id;

	public GameUser(String username, String password, Collection<? extends GrantedAuthority> authorities, String id) {
		super(username, password, authorities);
		this.id = id;
	}
}
