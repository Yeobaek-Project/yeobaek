package com.ssafy.yeobaek.artwork.dto.request;

import com.ssafy.yeobaek.common.enums.ArtCategory;
import com.ssafy.yeobaek.common.validation.ValidEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Schema(description = "작품 수정 요청 (모든 필드는 선택사항)")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateArtworkRequest {
    @Schema(description = "작품명 (1~100자)", example = "가을의 여백")
    @Size(min = 1, max = 100, message = "ARTWORK_INVALID_TITLE_LENGTH")
    private String title;

    @Schema(description = "작품 설명 (최대 500자)", example = "가을날 낙엽이 지는 풍경을 표현한 작품입니다.")
    @Size(max = 500, message = "ARTWORK_INVALID_DESCRIPTION_LENGTH")
    private String description;

    @Schema(description = "작품 가격 (원)", example = "550000")
    @Min(value = 0, message = "INVALID_INPUT_VALUE")
    private Integer price;

    @Schema(description = "카테고리 (LANDSCAPE, PORTRAIT, STILL_LIFE, GENRE, HISTORY, OTHER)", example = "LANDSCAPE")
    @ValidEnum(enumClass = ArtCategory.class, message = "ARTWORK_INVALID_CATEGORY")
    private String category;

    @Schema(description = "작품 가로 (cm)", example = "80")
    @Min(value = 1, message = "INVALID_INPUT_VALUE")
    @Max(value = 500, message = "INVALID_INPUT_VALUE")
    private Integer widthCm;

    @Schema(description = "작품 세로 (cm)", example = "60")
    @Min(value = 1, message = "INVALID_INPUT_VALUE")
    @Max(value = 500, message = "INVALID_INPUT_VALUE")
    private Integer heightCm;

    @Schema(description = "작품 무게 (kg)", example = "2.5")
    @Min(value = 0, message = "INVALID_INPUT_VALUE")
    private Double weightKg;

    @Schema(description = "제작 연도 (YYYY)", example = "2024")
    @Min(value = 1900, message = "INVALID_INPUT_VALUE")
    @Max(value = 2050, message = "INVALID_INPUT_VALUE")
    private Integer productionYear;

    @Schema(description = "작품 이미지 URL 목록 (최소 1장, 최대 10장)", example = "[\"https://example.com/image1.jpg\", \"https://example.com/image2.jpg\"]")
    @Size(min = 1, max = 10, message = "ARTWORK_INVALID_IMAGES_LENGTH")
    private List<String> images;
}
