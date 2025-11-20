package com.ssafy.yeobaek.cafe.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Schema(description = "전시 정보")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ExhibitionInfo {
    @Schema(description = "작품 ID", example = "1")
    private Long artworkId;

    @Schema(description = "작품명", example = "여름의 여백")
    private String artworkTitle;

    @Schema(description = "작품 썸네일 URL", example = "https://example.com/artwork1.jpg")
    private String artworkImageUrl;

    @Schema(description = "작가명", example = "홍길동")
    private String artistName;

    @Schema(description = "전시 시작일 (YYYY-MM-DD)", example = "2024-01-01")
    private String exhibitionStartDate;

    @Schema(description = "전시 종료일 (YYYY-MM-DD)", example = "2024-03-31")
    private String exhibitionEndDate;
}
