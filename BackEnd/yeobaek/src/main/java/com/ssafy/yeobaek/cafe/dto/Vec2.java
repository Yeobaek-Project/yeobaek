package com.ssafy.yeobaek.cafe.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Schema(description = "좌표 정보")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Vec2 {

    @Schema(description = "x 좌표", example = "1.2")
    @NotEmpty(message = "MISSING_REQUIRED_FIELD")
    private Double x;

    @Schema(description = "y 좌표", example = "3.4")
    @NotEmpty(message = "MISSING_REQUIRED_FIELD")
    private Double y;
}
