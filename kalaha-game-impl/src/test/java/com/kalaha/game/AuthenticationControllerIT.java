package com.kalaha.game;

import com.kalaha.game.controller.AuthenticationController;
import com.kalaha.game.dao.KalahaPlayerRepository;
import com.kalaha.game.model.Player;
import com.kalaha.game.security.AuthenticationService;
import com.kalaha.game.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("Test")
class AuthenticationControllerIT {

	@MockBean
	private AuthenticationService authenticationService;
	@Autowired
	private KalahaPlayerRepository kalahaPlayerRepository;

	@MockBean
	private JwtUtil jwtUtil;
	private MockMvc mockMvc;

	@BeforeEach
	void setUp() {
		mockMvc = MockMvcBuilders.standaloneSetup(new AuthenticationController(authenticationService))
				.build();
	}

	@Test
	void should_register_new_player() throws Exception {

		mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/users")
						.content("{\"userName\": \"name\",\"password\": \"password\"}")
						.contentType(MediaType.APPLICATION_JSON))
				.andDo(MockMvcResultHandlers.print())
				.andExpect(MockMvcResultMatchers.status().isCreated());
	}

	@Test
	void should_authenticate_player() throws Exception {

		kalahaPlayerRepository.save(Player.builder()
				.userName("test")
				.password("test123")
				.build());

		Mockito.when(jwtUtil.generateToken(ArgumentMatchers.any(UserDetails.class))).thenReturn("token");

		mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/users/login")

						.content("{\"userName\": \"test\",\"password\": \"test123\"}")
						.contentType(MediaType.APPLICATION_JSON))
				.andDo(MockMvcResultHandlers.print())
				.andExpect(MockMvcResultMatchers.status().isCreated());

	}
}