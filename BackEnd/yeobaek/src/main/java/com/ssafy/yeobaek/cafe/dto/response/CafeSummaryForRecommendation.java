package com.ssafy.yeobaek.cafe.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@Schema(description = "추천 카페 요약 정보")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CafeSummaryForRecommendation {

    @Schema(description = "카페 ID", example = "1")
    private Long cafeId;

    @Schema(description = "매장명", example = "카페 여백")
    private String cafeName;

    @Schema(description = "카페 주소", example = "서울시 강남구 테헤란로 123")
    private String address;

    @Schema(description = "대표 이미지 URL", example = "https://s3.presigned.url/cafe1.jpg")
    private String primaryImageUrl;

    @Schema(description = "인테리어 스타일 목록", example = "[\"모던\", \"미니멀\"]")
    private List<String> categories;

    @Schema(description = "총 평점 (리뷰 평균)", example = "4.5")
    private Double totalReviewScore;

    @Schema(description = "전시 슬롯 목록 (전시 가능한 슬롯만)")
    private List<Slot> slots;
}
