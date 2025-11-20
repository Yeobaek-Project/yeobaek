package com.ssafy.yeobaek.cafe.dto.request;

import com.ssafy.yeobaek.cafe.dto.Vec2;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Schema(description = "카페 공간 등록 요청")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateDisplayWallRequest {

	@Schema(description = "벽 이름", example = "벽 A")
	@NotBlank(message = "MISSING_REQUIRED_FIELD")
	@Size(min = 2, max = 15, message = "CAFE_INVALID_NAME_LENGTH")
	private String wallName;

	@Schema(description = "시작점")
	@NotNull(message = "MISSING_REQUIRED_FIELD")
	private Vec2 start;

	@Schema(description = "끝점")
	@NotNull(message = "MISSING_REQUIRED_FIELD")
	private Vec2 end;

	@Schema(description = "벽 너비 (cm)", example = "200.0")
	@NotNull(message = "MISSING_REQUIRED_FIELD")
	@Min(value = 0, message = "INVALID_INPUT_VALUE")
	@Max(value = 5000, message = "INVALID_INPUT_VALUE")
	private Double width;

	@Schema(description = "벽 높이 (cm)", example = "150.0")
	@Min(value = 0, message = "INVALID_INPUT_VALUE")
	@Max(value = 5000, message = "INVALID_INPUT_VALUE")
	private Double height;

	@Schema(description = "전시 가능 여부", example = "true")
	@NotNull(message = "MISSING_REQUIRED_FIELD")
	private Boolean status;
}
