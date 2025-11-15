package com.ssafy.yeobaek.exhibition.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Schema(description = "슬롯 간략 정보")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SlotDto {
    @Schema(description = "슬롯 ID (wallId)", example = "1")
    private Long slotId;

    @Schema(description = "벽 이름", example = "벽 A")
    private String wallName;

    @Schema(description = "벽 너비 (cm)", example = "300.0")
    private Double widthCm;

    @Schema(description = "벽 높이 (cm)", example = "200.0")
    private Double heightCm;

    @Schema(description = "수수료율 (%)", example = "10.0")
    private Double commissionRate;
}
