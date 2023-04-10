package com.kalaha.game;

import com.kalaha.game.model.KalahaErrorResponse;
import com.kalaha.game.model.KalahaGameRequest;
import com.kalaha.game.model.KalahaGameResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

@RequestMapping("/api/v1/kalaha-games")
@Tag(name = "Kalaha Game API")
public interface KalahaApi {

	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(HttpStatus.CREATED)
	@Operation(summary = "Create New Kalaha Game API.",
			description = "Api for creating new kalaha game instance. It returns response object with " +
					" new created game id, kalaha board and player turn",
			responses = {
					@ApiResponse(responseCode = "201", content = @Content(schema = @Schema(implementation =
							KalahaGameResponse.class))),
					@ApiResponse(responseCode = "400", content = @Content(schema = @Schema(implementation =
							KalahaErrorResponse.class))),
					@ApiResponse(responseCode = "404", content = @Content(schema = @Schema(implementation =
							KalahaErrorResponse.class))),
					@ApiResponse(responseCode = "500", content = @Content(schema = @Schema(implementation =
							KalahaErrorResponse.class)))
			})
	KalahaGameResponse createGame(@RequestBody(description = "Kalaha game request.",
			content = @Content(schema = @Schema(implementation = KalahaGameRequest.class))) KalahaGameRequest kalahaGameRequest);

	@PutMapping(value = "/{gameId}/pits/{pitId}")
	@Operation(summary = "Sowing Stones API.",
			description = "Api for sowing stones inside selected pit. It returns the game board after sowing stones " +
					"to other game pits",
			responses = {
					@ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation =
							KalahaGameResponse.class))),
					@ApiResponse(responseCode = "400", content = @Content(schema = @Schema(implementation =
							KalahaErrorResponse.class))),
					@ApiResponse(responseCode = "404", content = @Content(schema = @Schema(implementation =
							KalahaErrorResponse.class))),
					@ApiResponse(responseCode = "500", content = @Content(schema = @Schema(implementation =
							KalahaErrorResponse.class)))
			})
	KalahaGameResponse sow(@PathVariable String gameId, @PathVariable Integer pitId);

}
