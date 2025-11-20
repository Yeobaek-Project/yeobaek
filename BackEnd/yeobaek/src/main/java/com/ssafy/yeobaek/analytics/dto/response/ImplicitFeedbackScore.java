package com.ssafy.yeobaek.analytics.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 암묵적 피드백 점수 DTO
 * Collaborative Filtering을 위한 사용자-아이템 점수
 */
@Schema(description = "암묵적 피드백 점수")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImplicitFeedbackScore {

    @Schema(description = "사용자 ID", example = "1")
    private Long userId;

    @Schema(description = "아이템 ID (카페 또는 작품 ID)", example = "100")
    private Long itemId;

    @Schema(description = "암묵적 피드백 점수", example = "27.36")
    private Double score;
}
