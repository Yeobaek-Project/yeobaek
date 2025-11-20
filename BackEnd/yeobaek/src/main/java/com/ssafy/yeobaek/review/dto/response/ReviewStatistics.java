package com.ssafy.yeobaek.review.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Schema(description = "평가 통계 정보")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewStatistics {

    @Schema(description = "전체 평가 수", example = "42")
    private Integer totalReviewCount;

    @Schema(description = "리뷰 평균 평점", example = "4.5")
    private Double totalReviewScore;

    @Schema(description = "각 별점별 통계")
    private List<RatingStat> ratingStats;
}
