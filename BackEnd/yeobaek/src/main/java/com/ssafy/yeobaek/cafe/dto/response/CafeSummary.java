package com.ssafy.yeobaek.cafe.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Schema(description = "카페 목록 요약 정보")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CafeSummary {
    @Schema(description = "카페 ID", example = "1")
    private Long cafeId;

    @Schema(description = "매장명", example = "카페 여백")
    private String cafeName;

    @Schema(description = "카페 주소", example = "서울시 강남구 테헤란로 123")
    private String address;

    @Schema(description = "위도", example = "37.5665")
    private Double latitude;

    @Schema(description = "경도", example = "126.9780")
    private Double longitude;

    @Schema(description = "대표 이미지 URL", example = "https://example.com/cafe1.jpg")
    private String primaryImageUrl;

    @Schema(description = "전시 가능한 슬롯 수", example = "3")
    private Integer availableSlotCount;

    @Schema(description = "전체 슬롯 수", example = "5")
    private Integer totalSlotCount;

    @Schema(description = "거리 (km)", example = "1.5")
    private Double distance;

    @Schema(description = "인테리어 스타일 목록", example = "[ \"모던\", \"미니멀\" ]")
    private List<String> categories;
}
