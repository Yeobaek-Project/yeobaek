package com.ssafy.yeobaek.review.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Schema(description = "평가 상세 정보")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewDetail {

    @Schema(description = "평가 ID", example = "1")
    private Long reviewId;

    @Schema(description = "리뷰 작성자 정보")
    private Reviewer reviewer;

    @Schema(description = "평가 점수", example = "5")
    private Integer rating;

    @Schema(description = "코멘트", example = "정말 좋은 전시였습니다!")
    private String comment;

    @Schema(description = "평가 작성일", example = "2024-01-15")
    private LocalDate createdAt;
}
