package com.ssafy.yeobaek.user.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Schema(description = "평가 통계")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RatingStatsData {
    @Schema(description = "전체 평가 수", example = "42")
    private Integer totalReviews;

    @Schema(description = "평점", example = "4.5")
    private Double averageRating;
}
