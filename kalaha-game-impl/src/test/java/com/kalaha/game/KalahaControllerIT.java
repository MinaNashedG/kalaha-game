package com.kalaha.game;

import com.kalaha.game.controller.KalahaController;
import com.kalaha.game.dao.KalahaGameRepository;
import com.kalaha.game.dao.KalahaPlayerRepository;
import com.kalaha.game.model.GameStatus;
import com.kalaha.game.model.KalahaGame;
import com.kalaha.game.model.Player;
import com.kalaha.game.security.JwtUtil;
import com.kalaha.game.security.UserContext;
import com.kalaha.game.service.KalahaGameService;
import com.kalaha.game.service.KalahaSowService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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

import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("Test")
class KalahaControllerIT {
	public static final String PLAYER_ONE = "1";
	public static final String PLAYER_TWO = "2";
	public static final List<Player> PLAYERS = List.of(Player.builder().id(PLAYER_ONE).build(),
			Player.builder().id(PLAYER_TWO).build());
	@Autowired
	private KalahaGameService kalahaGameService;
	@Autowired
	private KalahaSowService kalahaSowService;

	@MockBean
	private JwtUtil jwtUtil;

	@MockBean
	private UserContext userContext;
	@Autowired
	private KalahaGameRepository kalahaGameRepository;

	@Autowired
	private KalahaPlayerRepository kalahaPlayerRepository;
	private MockMvc mockMvc;

	@BeforeEach
	void setUp() {
		mockMvc = MockMvcBuilders.standaloneSetup(new KalahaController(kalahaGameService, kalahaSowService))
				.build();
		lenient().when(jwtUtil.validateToken("token", PLAYER_ONE)).thenReturn(true);
		lenient().when(userContext.getUserId()).thenReturn(PLAYER_ONE);
		kalahaPlayerRepository.saveAll(PLAYERS);
	}

	@Test
	void should_create_game() throws Exception {

		mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/kalaha-games")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"numberOfPits\": 6, \"numberOfStones\": 6,\"opponent\": \"2\"}")
						.header("Authorization", "Bearer token"))
				.andDo(MockMvcResultHandlers.print())
				.andExpect(MockMvcResultMatchers.status().isCreated())
				.andExpect(MockMvcResultMatchers.content()
						.json("{\"board\":[6,6,6,6,6,6,0,6,6,6,6,6,6,0]," +
								"\"playerTurn\":\"1\",\"status\":\"NEW\",\"bonusTurn\":false," +
								"\"players\":[{\"id\":\"1\",\"userName\":null,\"email\":null},{\"id\":\"2\"," +
								"\"userName\":null,\"email\":null}]}"));
	}

	@Test
	void should_create_game_with_4_pits_5_stones() throws Exception {

		mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/kalaha-games")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"numberOfPits\": 4, \"numberOfStones\": 5,\"opponent\": \"2\"}")
						.header("Authorization", "Bearer token"))
				.andDo(MockMvcResultHandlers.print())
				.andExpect(MockMvcResultMatchers.status().isCreated())
				.andExpect(MockMvcResultMatchers.content()
						.json("{\"board\":[5,5,5,5,0,5,5,5,5,0]," +
								"\"playerTurn\":\"1\",\"status\":\"NEW\",\"bonusTurn\":false," +
								"\"players\":[{\"id\":\"1\",\"userName\":null,\"email\":null},{\"id\":\"2\"," +
								"\"userName\":null,\"email\":null}]}"));
	}

	@Test
	void should_throw_400_when_number_of_pits_are_invalid() throws Exception {

		mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/kalaha-games")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"numberOfPits\": 0,\"opponent\": \"2\"}")
						.header("Authorization", "Bearer token"))
				.andDo(MockMvcResultHandlers.print())
				.andExpect(MockMvcResultMatchers.status().isBadRequest());
	}

	@Test
	void should_throw_400_when_number_of_stones_are_invalid() throws Exception {

		mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/kalaha-games")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"numberOfStones\": 1000,\"opponent\": \"2\"}")
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
				.playerTurn(PLAYER_ONE)
				.playerTurnIndex(0)
				.players(PLAYERS)
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
								"\"playerTurn\":\"2\",\"status\":\"IN_PROGRESS\",\"bonusTurn\":false," +
								"\"players\":[{\"id\":\"1\",\"userName\":null,\"email\":null},{\"id\":\"2\"," +
								"\"userName\":null,\"email\":null}]}"));
	}

	@Test
	void should_return_board_after_capture_stones() throws Exception {
		KalahaGame game = kalahaGameRepository.save(KalahaGame.builder()
				.board(List.of(1, 0, 6, 6, 6, 6, 0, 6, 6, 6, 6, 6, 6, 0))
				.status(GameStatus.NEW)
				.numberOfPits(6)
				.numberOfPlayers(2)
				.numberOfStones(6)
				.playerTurn(PLAYER_ONE)
				.playerTurnIndex(0)
				.players(PLAYERS)
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
								"\"playerTurn\":\"2\",\"status\":\"IN_PROGRESS\",\"bonusTurn\":false," +
								"\"players\":[{\"id\":\"1\",\"userName\":null,\"email\":null},{\"id\":\"2\"," +
								"\"userName\":null,\"email\":null}]}"));
	}

	@Test
	void should_not_capture_stones_when_opposite_has_zero_stones() throws Exception {
		KalahaGame game = kalahaGameRepository.save(KalahaGame.builder()
				.board(List.of(1, 0, 6, 6, 6, 6, 0, 6, 6, 6, 6, 0, 6, 0))
				.status(GameStatus.NEW)
				.numberOfPits(6)
				.numberOfPlayers(2)
				.numberOfStones(6)
				.playerTurn(PLAYER_ONE)
				.playerTurnIndex(0)
				.players(PLAYERS)
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
								"\"playerTurn\":\"2\",\"status\":\"IN_PROGRESS\",\"bonusTurn\":false," +
								"\"players\":[{\"id\":\"1\",\"userName\":null,\"email\":null},{\"id\":\"2\"," +
								"\"userName\":null,\"email\":null}]}"));
	}

	@Test
	void should_return_game_over_and_the_second_player_winner() throws Exception {
		//GIVEN
		when(userContext.getUserId()).thenReturn(PLAYER_TWO);

		KalahaGame game = kalahaGameRepository.save(KalahaGame.builder()
				.board(List.of(1, 0, 6, 6, 6, 6, 10, 0, 0, 0, 0, 0, 1, 26))
				.status(GameStatus.IN_PROGRESS)
				.numberOfPits(6)
				.numberOfPlayers(2)
				.numberOfStones(6)
				.playerTurn(PLAYER_TWO)
				.playerTurnIndex(1)
				.startPit(7)
				.players(PLAYERS)
				.endPit(13)
				.build());

		mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/kalaha-games/" + game.getId() + "/pits/12")
						.contentType(MediaType.APPLICATION_JSON)
						.header("Authorization", "Bearer token"))
				.andDo(MockMvcResultHandlers.print())
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content()
						.json("{\"board\":[1, 0, 6, 6, 6, 6, 10, 0, 0, 0, 0, 0, 0, 27]," +
								"\"playerTurn\":\"2\",\"status\":\"OVER\",\"bonusTurn\":true,\"winner\":\"2\"," +
								"\"players\":[{\"id\":\"1\",\"userName\":null,\"email\":null},{\"id\":\"2\"," +
								"\"userName\":null,\"email\":null}]}"));
	}

	@Test
	void should_return_error_invalid_player_turn() throws Exception {
		KalahaGame game = kalahaGameRepository.save(KalahaGame.builder()
				.board(List.of(1, 0, 6, 6, 6, 6, 0, 6, 6, 6, 6, 6, 6, 0))
				.status(GameStatus.NEW)
				.numberOfPits(6)
				.numberOfPlayers(2)
				.numberOfStones(6)
				.playerTurn(PLAYER_ONE)
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