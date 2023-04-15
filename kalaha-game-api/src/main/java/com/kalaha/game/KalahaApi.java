package com.kalaha.game;

import com.kalaha.game.model.KalahaErrorResponse;
import com.kalaha.game.model.KalahaGameRequest;
import com.kalaha.game.model.KalahaGameResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

@RequestMapping("/api/v1/kalaha-games")
@SecurityScheme(
		name = "kalaha",
		type = SecuritySchemeType.HTTP,
		scheme = "kalaha",
		bearerFormat = "JWT"
)
@Tag(name = "Kalaha Game API")
public interface KalahaApi {

	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(HttpStatus.CREATED)
	@Operation(summary = "Create New Kalaha Game API.",
			security = {@SecurityRequirement(name = "kalaha")},
			description = "Api for creating new kalaha game instance. It returns response object with " +
					" new created game id, kalaha board and player turn",
			responses = {
					@ApiResponse(responseCode = "201", content = @Content(schema = @Schema(implementation =
							KalahaGameResponse.class))),
					@ApiResponse(responseCode = "400", content = @Content(schema = @Schema(implementation =
							KalahaErrorResponse.class),
							examples =
							@ExampleObject(value = "{\"code\":VAL_006,\"message\":\"Invalid Request\"}"))),
					@ApiResponse(responseCode = "404", content = @Content(schema = @Schema(implementation =
							KalahaErrorResponse.class),
							examples =
							@ExampleObject(value = "{\"code\":VAL_003,\"message\":\"Resource not found\"}"))),
					@ApiResponse(responseCode = "500", content = @Content(schema = @Schema(implementation =
							KalahaErrorResponse.class),
							examples =
							@ExampleObject(value = "{\"code\":GeneralException,\"message\":\"Internal server error\"}"))),
					@ApiResponse(responseCode = "401", content = @Content(schema = @Schema(implementation =
							KalahaErrorResponse.class),
							examples =
							@ExampleObject(value = "{\"code\":Unauthorized,\"message\":\"Not authorized user \"}"))),
					@ApiResponse(responseCode = "403", content = @Content(schema = @Schema(implementation =
							KalahaErrorResponse.class),
							examples = @ExampleObject(value = "{\"code\":Forbidden,\"message\":\"Access denied\"}")))
			})
	KalahaGameResponse createGame(@RequestBody(description = "Kalaha game request.",
			content = @Content(schema = @Schema(implementation = KalahaGameRequest.class))) KalahaGameRequest kalahaGameRequest);

	@PutMapping(value = "/{gameId}/pits/{pitId}", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Sowing Stones API.",
			security = {@SecurityRequirement(name = "kalaha")},
			description = "Api for sowing stones inside selected pit. It returns the game board after sowing stones " +
					"to other game pits",
			responses = {
					@ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation =
							KalahaGameResponse.class))),
					@ApiResponse(responseCode = "400", content = @Content(schema = @Schema(implementation =
							KalahaErrorResponse.class),
							examples =
							@ExampleObject(value = "{\"code\":VAL_002,\"message\":\"Invalid Player Turn\"}"))),
					@ApiResponse(responseCode = "404", content = @Content(schema = @Schema(implementation =
							KalahaErrorResponse.class),
							examples =
							@ExampleObject(value = "{\"code\":VAL_003,\"message\":\"Resource not found\"}"))),
					@ApiResponse(responseCode = "500", content = @Content(schema = @Schema(implementation =
							KalahaErrorResponse.class),
							examples =
							@ExampleObject(value = "{\"code\":GeneralException,\"message\":\"Internal server error\"}"))),
					@ApiResponse(responseCode = "401", content = @Content(schema = @Schema(implementation =
							KalahaErrorResponse.class),
							examples =
							@ExampleObject(value = "{\"code\":Unauthorized,\"message\":\"Not authorized user \"}"))),
					@ApiResponse(responseCode = "403", content = @Content(schema = @Schema(implementation =
							KalahaErrorResponse.class),
							examples = @ExampleObject(value = "{\"code\":Forbidden,\"message\":\"Access denied\"}")))
			})
	KalahaGameResponse sow(@Parameter(
			name = "Authorization",
			description = "Access token",
			required = true,
			in = ParameterIn.HEADER,
			schema = @Schema(type = "string", format = "JWT"))
	@PathVariable String gameId, @PathVariable Integer pitId);
}
