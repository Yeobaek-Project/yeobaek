package com.ssafy.yeobaek.artwork.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Schema(description = "이미지 정보")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ImageRequest {
    @Schema(description = "이미지 URL", example = "https://example.com/image1.jpg")
    private String imageUrl;

    @Schema(description = "대표 이미지 여부 (1개만 true)", example = "true")
    private Boolean isPrimary;

    @Schema(description = "표시 순서 (1부터 시작)", example = "1")
    private Integer displayOrder;
}
