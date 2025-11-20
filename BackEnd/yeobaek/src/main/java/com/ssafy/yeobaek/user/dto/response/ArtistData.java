package com.ssafy.yeobaek.user.dto.response;

import com.ssafy.yeobaek.common.enums.ArtCategory;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Schema(description = "작가 정보")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ArtistData {
    @Schema(description = "작가 소개", example = "현대 미술을 전공한 신진 작가입니다.")
    private String introduction;

    @Schema(description = "작가의 대표 작품 카테고리 (작품 등록 개수 기준)", example = "LANDSCAPE")
    private ArtCategory category;

    @Schema(description = "등록한 작품 수", example = "15")
    private Integer artworkCount;

    @Schema(description = "현재 전시 중인 작품 수", example = "5")
    private Integer exhibitingCount;

    @Schema(description = "총 전시한 작품 수", example = "12")
    private Integer totalExhibitedCount;

    @Schema(description = "평가 통계")
    private RatingStatsData ratingStats;
}
