package com.ssafy.yeobaek.exhibition.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.ssafy.yeobaek.common.enums.ExhibitionState;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDate;

@Schema(description = "전시 일정 간략 정보 응답")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ExhibitionSummaryResponse {
    @Schema(description = "전시 ID", example = "1")
    private Long exhibitionId;

    @Schema(description = "카페 정보")
    private CafeDto cafe;

    @Schema(description = "작품 정보")
    private ArtworkDto artwork;

    @Schema(description = "슬롯 정보")
    private SlotDto slot;

    @Schema(description = "전시 시작일", example = "2025-11-15")
    private LocalDate startDate;

    @Schema(description = "전시 종료일", example = "2025-12-15")
    private LocalDate endDate;

    @Schema(description = "전시 상태", example = "ONGOING")
    private ExhibitionState exhibitionState;

    @Schema(description = "수수료율 (%)", example = "10.0")
    private Double commissionRate;

    @Schema(description = "추가 요청사항", example = "작품 조명은 자연광으로 부탁드립니다.")
    private String requirements;
}
