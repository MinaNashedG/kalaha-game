package com.kalaha.game.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "kalaha error response")
public class KalahaErrorResponse {

	@Schema(description = "Code of the error", example = "VAL001")
	private String code;

	@Schema(description = "The error message description.", example = "Invalid player turn.")
	private String message;

}
