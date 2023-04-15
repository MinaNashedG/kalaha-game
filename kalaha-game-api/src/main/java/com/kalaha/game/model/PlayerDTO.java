package com.kalaha.game.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class PlayerDTO {

	private String id;
	private String userName;
	private String password;
	private String email;
}
