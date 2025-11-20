package com.ssafy.yeobaek.review.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "별점별 통계")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RatingStat {

    @Schema(description = "별점", example = "5")
    private Integer rating;

    @Schema(description = "개수", example = "10")
    private Integer count;

    @Schema(description = "비율", example = "25.5")
    private Double percentage;
}
