package com.ssafy.yeobaek.artwork.dto.request;

import com.ssafy.yeobaek.common.enums.ArtCategory;
import com.ssafy.yeobaek.common.validation.ValidEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Schema(description = "작품 등록 요청")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateArtworkRequest {
    @Schema(description = "작품명 (1~100자)", example = "여름의 여백", required = true)
    @NotBlank(message = "MISSING_REQUIRED_FIELD")
    @Size(min = 1, max = 100, message = "ARTWORK_INVALID_TITLE_LENGTH")
    private String title;

    @Schema(description = "작품 설명 (최대 500자)", example = "여름날 햇살이 비치는 풍경을 표현한 작품입니다.")
    @NotBlank(message = "MISSING_REQUIRED_FIELD")
    @Size(max = 500, message = "ARTWORK_INVALID_DESCRIPTION_LENGTH")
    private String description;

    @Schema(description = "작품 가격 (원)", example = "500000", required = true)
    @NotNull(message = "MISSING_REQUIRED_FIELD")
    private Integer price;

    @Schema(description = "카테고리 (LANDSCAPE, PORTRAIT, STILL_LIFE, GENRE, HISTORY, OTHER)", example = "LANDSCAPE", required = true)
    @NotBlank(message = "MISSING_REQUIRED_FIELD")
    @ValidEnum(enumClass = ArtCategory.class, message = "ARTWORK_INVALID_CATEGORY")
    private String category;

    @Schema(description = "작품 가로 (cm)", example = "80", required = true)
    @NotNull(message = "MISSING_REQUIRED_FIELD")
    private Integer widthCm;

    @Schema(description = "작품 세로 (cm)", example = "60", required = true)
    @NotNull(message = "MISSING_REQUIRED_FIELD")
    private Integer heightCm;

    @Schema(description = "작품 무게 (kg)", example = "2.5", required = true)
    @NotNull(message = "MISSING_REQUIRED_FIELD")
    private Double weightKg;

    @Schema(description = "제작 연도 (YYYY)", example = "2024", required = true)
    @NotNull(message = "MISSING_REQUIRED_FIELD")
    private Integer productionYear;

    @Schema(description = "작품 이미지 URL 목록 (최소 1장, 최대 10장, 배열 순서대로 첫 번째가 대표 이미지)", example = "[\"https://example.com/image1.jpg\", \"https://example.com/image2.jpg\"]", required = true)
    @NotNull(message = "MISSING_REQUIRED_FIELD")
    @Size(min = 1, max = 10, message = "ARTWORK_INVALID_IMAGES_LENGTH")
    private List<String> images;
}
