package com.ssafy.yeobaek.review.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Schema(description = "전시 리뷰 작성 요청")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateReviewRequest {

    @Schema(description = "전시 ID", example = "1", required = true)
    @NotNull(message = "MISSING_REQUIRED_FIELD")
    private Long exhibitionId;

    @Schema(description = "평가 대상자 ID", example = "2", required = true)
    @NotNull(message = "MISSING_REQUIRED_FIELD")
    private Long revieweeId;

    @Schema(description = "평점 (1~5)", example = "5", required = true)
    @NotNull(message = "MISSING_REQUIRED_FIELD")
    @Min(value = 1, message = "REVIEW_INVALID_RATING")
    @Max(value = 5, message = "REVIEW_INVALID_RATING")
    private Integer rating;

    @Schema(description = "후기 (최대 100자)", example = "정말 멋진 전시였습니다!")
    @Size(max = 100, message = "REVIEW_INVALID_COMMENT_LENGTH")
    private String comment;
}
