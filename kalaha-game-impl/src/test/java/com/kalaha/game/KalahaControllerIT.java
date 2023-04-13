package com.kalaha.game;

import com.kalaha.game.controller.KalahaController;
import com.kalaha.game.dao.KalahaGameRepository;
import com.kalaha.game.model.GameStatus;
import com.kalaha.game.model.KalahaGame;
import com.kalaha.game.security.JwtUtil;
import com.kalaha.game.service.KalahaGameService;
import com.kalaha.game.service.KalahaSowService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("Test")
class KalahaControllerIT {
	@Autowired
	private KalahaGameService kalahaGameService;
	@Autowired
	private KalahaSowService kalahaSowService;

	@MockBean
	private JwtUtil jwtUtil;
	@Autowired
	private KalahaGameRepository kalahaGameRepository;
	private MockMvc mockMvc;

	@BeforeEach
	void setUp() {
		mockMvc = MockMvcBuilders.standaloneSetup(new KalahaController(kalahaGameService, kalahaSowService))
				.build();
		Mockito.lenient().when(jwtUtil.validateToken("token", "testUserName")).thenReturn(true);
	}

	@Test
	void should_create_game() throws Exception {

		mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/kalaha-games")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"numberOfPits\": 6, \"numberOfStones\": 6}")
						.header("Authorization", "Bearer token"))
				.andDo(MockMvcResultHandlers.print())
				.andExpect(MockMvcResultMatchers.status().isCreated())
				.andExpect(MockMvcResultMatchers.content()
						.json("{\"board\":[6,6,6,6,6,6,0,6,6,6,6,6,6,0]," +
								"\"playerTurn\":1,\"status\":\"NEW\",\"bonusTurn\":false}"));
	}

	@Test
	void should_create_game_with_4_pits_5_stones() throws Exception {

		mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/kalaha-games")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"numberOfPits\": 4, \"numberOfStones\": 5}")
						.header("Authorization", "Bearer token"))
				.andDo(MockMvcResultHandlers.print())
				.andExpect(MockMvcResultMatchers.status().isCreated())
				.andExpect(MockMvcResultMatchers.content()
						.json("{\"board\":[5,5,5,5,0,5,5,5,5,0]," +
								"\"playerTurn\":1,\"status\":\"NEW\",\"bonusTurn\":false}"));
	}

	@Test
	void should_throw_400_when_number_of_pits_are_invalid() throws Exception {

		mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/kalaha-games")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"numberOfPits\": 0}")
						.header("Authorization", "Bearer token"))
				.andDo(MockMvcResultHandlers.print())
				.andExpect(MockMvcResultMatchers.status().isBadRequest());
	}

	@Test
	void should_throw_400_when_number_of_stones_are_invalid() throws Exception {

		mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/kalaha-games")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"numberOfStones\": 1000}")
						.header("Authorization", "Bearer token"))
				.andDo(MockMvcResultHandlers.print())
				.andExpect(MockMvcResultMatchers.status().isBadRequest());
	}

	@Test
	void should_return_board_after_sowing_stones() throws Exception {
		KalahaGame game = kalahaGameRepository.save(KalahaGame.builder()
				.board(List.of(6, 6, 6, 6, 6, 6, 0, 6, 6, 6, 6, 6, 6, 0))
				.status(GameStatus.NEW)
				.numberOfPits(6)
				.numberOfPlayers(2)
				.numberOfStones(6)
				.playerTurn(1)
				.startPit(0)
				.endPit(6)
				.build());

		mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/kalaha-games/" + game.getId() + "/pits/1")
						.contentType(MediaType.APPLICATION_JSON)
						.header("Authorization", "Bearer token"))
				.andDo(MockMvcResultHandlers.print())
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content()
						.json("{\"board\":[6,0,7,7,7,7,1,7,6,6,6,6,6,0]," +
								"\"playerTurn\":2,\"status\":\"IN_PROGRESS\",\"bonusTurn\":false}"));
	}

	@Test
	void should_return_board_after_capture_stones() throws Exception {
		KalahaGame game = kalahaGameRepository.save(KalahaGame.builder()
				.board(List.of(1, 0, 6, 6, 6, 6, 0, 6, 6, 6, 6, 6, 6, 0))
				.status(GameStatus.NEW)
				.numberOfPits(6)
				.numberOfPlayers(2)
				.numberOfStones(6)
				.playerTurn(1)
				.startPit(0)
				.endPit(6)
				.build());

		mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/kalaha-games/" + game.getId() + "/pits/0")
						.contentType(MediaType.APPLICATION_JSON)
						.header("Authorization", "Bearer token"))
				.andDo(MockMvcResultHandlers.print())
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content()
						.json("{\"board\":[0,0,6,6,6,6,7,0,6,6,6,6,6,0]," +
								"\"playerTurn\":2,\"status\":\"IN_PROGRESS\",\"bonusTurn\":false}"));
	}

	@Test
	void should_not_capture_stones_when_opposite_has_zero_stones() throws Exception {
		KalahaGame game = kalahaGameRepository.save(KalahaGame.builder()
				.board(List.of(1, 0, 6, 6, 6, 6, 0, 6, 6, 6, 6, 0, 6, 0))
				.status(GameStatus.NEW)
				.numberOfPits(6)
				.numberOfPlayers(2)
				.numberOfStones(6)
				.playerTurn(1)
				.startPit(0)
				.endPit(6)
				.build());

		mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/kalaha-games/" + game.getId() + "/pits/0")
						.contentType(MediaType.APPLICATION_JSON)
						.header("Authorization", "Bearer token"))
				.andDo(MockMvcResultHandlers.print())
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content()
						.json("{\"board\":[0,1,6,6,6,6,0,6,6,6,6,0,6,0]," +
								"\"playerTurn\":2,\"status\":\"IN_PROGRESS\",\"bonusTurn\":false}"));
	}

	@Test
	void should_return_game_over_and_the_second_player_winner() throws Exception {
		KalahaGame game = kalahaGameRepository.save(KalahaGame.builder()
				.board(List.of(1, 0, 6, 6, 6, 6, 10, 0, 0, 0, 0, 0, 1, 26))
				.status(GameStatus.IN_PROGRESS)
				.numberOfPits(6)
				.numberOfPlayers(2)
				.numberOfStones(6)
				.playerTurn(2)
				.startPit(7)
				.endPit(13)
				.build());

		mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/kalaha-games/" + game.getId() + "/pits/12")
						.contentType(MediaType.APPLICATION_JSON)
						.header("Authorization", "Bearer token"))
				.andDo(MockMvcResultHandlers.print())
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content()
						.json("{\"board\":[1, 0, 6, 6, 6, 6, 10, 0, 0, 0, 0, 0, 0, 27]," +
								"\"playerTurn\":2,\"status\":\"OVER\",\"bonusTurn\":true,\"playerWin\":2}"));
	}

	@Test
	void should_return_error_invalid_player_turn() throws Exception {
		KalahaGame game = kalahaGameRepository.save(KalahaGame.builder()
				.board(List.of(1, 0, 6, 6, 6, 6, 0, 6, 6, 6, 6, 6, 6, 0))
				.status(GameStatus.NEW)
				.numberOfPits(6)
				.numberOfPlayers(2)
				.numberOfStones(6)
				.playerTurn(1)
				.startPit(0)
				.endPit(6)
				.build());

		mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/kalaha-games/" + game.getId() + "/pits/10")
						.contentType(MediaType.APPLICATION_JSON)
						.header("Authorization", "Bearer token"))
				.andDo(MockMvcResultHandlers.print())
				.andExpect(MockMvcResultMatchers.status().isBadRequest());
	}
}