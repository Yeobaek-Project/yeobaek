package com.ssafy.yeobaek.chat.dto.response;

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

    @Schema(description = "전시 ID", example = "1")
    private Long exhibitionId;

    @Schema(description = "카페명", example = "여름의 여백")
    private String cafeName;

    @Schema(description = "슬롯명", example = "여름의 여백")
    private String slotName;

    @Schema(description = "작품명", example = "여름의 여백")
    private String artworkTitle;

    @Schema(description = "전시 시작일 (YYYY-MM-DD)", example = "2024-01-01")
    private String exhibitionStartDate;

    @Schema(description = "전시 종료일 (YYYY-MM-DD)", example = "2024-03-31")
    private String exhibitionEndDate;

    @Schema(description = "제안 수수료율 (%)", example = "10.0")
    private Double commissionRate;

    @Schema(description = "기타 요청 사항", example = "홍보용 작가 소개 카드 또는 작품 설명 자료를 함께 제공해주세요.")
    private String requirements;
}
