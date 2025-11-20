package com.ssafy.yeobaek.cafe.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Schema(description = "슬롯 정보 수정 요청")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateSlotRequest {

    @Schema(description = "수수료율 (%)", example = "10.0")
    @Min(value = 0, message = "SLOT_INVALID_COMMISSION_RATE")
    @Max(value = 15, message = "SLOT_INVALID_COMMISSION_RATE")
    private Double commissionRate;

    @Schema(description = "기타 요청 사항", example = "홍보용 작가 소개 카드 또는 작품 설명 자료를 함께 제공해주세요.")
    @Size(max = 500, message = "SLOT_INVALID_REQUIREMENT_LENGTH")
    private String requirements;
}