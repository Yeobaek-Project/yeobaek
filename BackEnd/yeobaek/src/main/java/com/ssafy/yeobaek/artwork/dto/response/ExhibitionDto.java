package com.ssafy.yeobaek.artwork.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Schema(description = "전시 정보")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExhibitionDto {
    @Schema(description = "카페 ID", example = "1")
    private Long cafeId;

    @Schema(description = "카페명", example = "카페 여백")
    private String cafeName;

    @Schema(description = "카페 주소", example = "서울시 강남구 테헤란로 123")
    private String cafeAddress;

    @Schema(description = "카페 이미지 URL", example = "https://example.com/cafe.jpg")
    private String cafeImageUrl;

    @Schema(description = "전시 시작일 (YYYY-MM-DD)", example = "2024-01-01")
    private LocalDate exhibitionStartDate;

    @Schema(description = "전시 종료일 (YYYY-MM-DD)", example = "2024-03-31")
    private LocalDate exhibitionEndDate;
}
