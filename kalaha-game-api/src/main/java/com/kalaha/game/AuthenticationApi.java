package com.kalaha.game;

import com.kalaha.game.model.AuthenticatedPlayer;
import com.kalaha.game.model.KalahaErrorResponse;
import com.kalaha.game.model.PlayerDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

@RequestMapping("/api/v1/users")
@Tag(name = "Authentication API")
public interface AuthenticationApi {

	@PostMapping(value = "/login", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(HttpStatus.CREATED)
	@Operation(summary = "Registration Player API.",
			description = "API for Kalaha Game new Player Registration",
			responses = {
					@ApiResponse(responseCode = "201"),
					@ApiResponse(responseCode = "404", content = @Content(schema = @Schema(implementation =
							KalahaErrorResponse.class),
							examples =
							@ExampleObject(value = "{\"code\":VAL_003,\"message\":\"Resource not found\"}"))),
					@ApiResponse(responseCode = "500", content = @Content(schema = @Schema(implementation =
							KalahaErrorResponse.class),
							examples = @ExampleObject(value = "{\"code\":GeneralException,\"message\":\"Internal " +
									"server error\"}"))),
					@ApiResponse(responseCode = "401", content = @Content(schema = @Schema(implementation =
							KalahaErrorResponse.class),
							examples =
							@ExampleObject(value = "{\"code\":Unauthorized,\"message\":\"Player authentication " +
									"failed" +
									".\"}"))),
			})
	AuthenticatedPlayer login(@RequestBody(description = "Player authentication request.",
			content = @Content(schema = @Schema(implementation = PlayerDTO.class))) PlayerDTO playerDTO) throws Exception;


	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(HttpStatus.CREATED)
	@Operation(summary = "Registration Player API.",
			description = "API for Kalaha Game new Player Registration",
			responses = {
					@ApiResponse(responseCode = "201"),
					@ApiResponse(responseCode = "404", content = @Content(schema = @Schema(implementation =
							KalahaErrorResponse.class),
							examples =
							@ExampleObject(value = "{\"code\":VAL_003,\"message\":\"Resource not found\"}"))),
					@ApiResponse(responseCode = "500", content = @Content(schema = @Schema(implementation =
							KalahaErrorResponse.class),
							examples = @ExampleObject(value = "{\"code\":GeneralException,\"message\":\"Internal " +
									"server error\"}"))),
					@ApiResponse(responseCode = "400", content = @Content(schema = @Schema(implementation =
							KalahaErrorResponse.class),
							examples =
							@ExampleObject(value = "{\"code\":VAL_007,\"message\":\"User is already exits.\"}"))),
			})
	void register(@RequestBody(description = "Player registration request.",
			content = @Content(schema = @Schema(implementation = PlayerDTO.class))) PlayerDTO playerDTO);

}
