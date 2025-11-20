package com.ssafy.yeobaek.artwork.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Schema(description = "작품 요약 정보")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ArtworkSummaryDto {
    @Schema(description = "작품 ID", example = "1")
    private Long artworkId;

    @Schema(description = "작품명", example = "여름의 여백")
    private String title;

    @Schema(description = "썸네일 이미지 URL", example = "https://example.com/thumbnail.jpg")
    private String thumbnailUrl;

    @Schema(description = "작품 가격 (원)", example = "500000")
    private Integer price;

    @Schema(description = "카테고리", example = "PAINTING")
    private String category;

    @Schema(description = "작품 가로 (cm)", example = "100")
    private Integer widthCm;

    @Schema(description = "작품 세로 (cm)", example = "150")
    private Integer heightCm;

    @Schema(description = "작품 무게 (kg)", example = "5.5")
    private Double weightKg;

    @Schema(description = "제작 연도", example = "2024")
    private Integer productionYear;

    @Schema(description = "전시 중 여부", example = "false")
    private Boolean isExhibiting;

    @Schema(description = "북마크 수", example = "42")
    private Integer bookmarkCount;

    @Schema(description = "전시 장소(카페명)", example = "카페 여백")
    private String cafeName;

    @Schema(description = "작가 프로필")
    private ArtistDto artist;
}
