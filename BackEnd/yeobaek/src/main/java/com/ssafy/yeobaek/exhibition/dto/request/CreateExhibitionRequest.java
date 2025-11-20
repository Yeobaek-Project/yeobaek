package com.ssafy.yeobaek.exhibition.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Schema(description = "전시 제안 생성 요청")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateExhibitionRequest {

    @Schema(description = "카페 ID", example = "1", required = true)
    @NotNull(message = "MISSING_REQUIRED_FIELD")
    private Long cafeId;

    @Schema(description = "작품 ID", example = "123", required = true)
    @NotNull(message = "MISSING_REQUIRED_FIELD")
    private Long artworkId;

    @Schema(description = "전시 슬롯 ID", example = "5", required = true)
    @NotNull(message = "MISSING_REQUIRED_FIELD")
    private Long slotId;

    @Schema(description = "전시 시작일 (YYYY-MM-DD)", example = "2025-03-01", required = true)
    @NotNull(message = "MISSING_REQUIRED_FIELD")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @Schema(description = "전시 종료일 (YYYY-MM-DD)", example = "2025-05-31", required = true)
    @NotNull(message = "MISSING_REQUIRED_FIELD")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    @Schema(description = "합의된 수수료율 (%, 0~15)", example = "10.0", required = true)
    @NotNull(message = "MISSING_REQUIRED_FIELD")
    @Min(value = 0, message = "EXHIBITION_INVALID_COMMISSION_RATE")
    @Max(value = 15, message = "EXHIBITION_INVALID_COMMISSION_RATE")
    private Double commissionRate;

    @Schema(description = "추가 조건 및 요구사항", example = "작품 운반비는 카페에서 부담합니다.")
    @Size(max = 500, message = "EXHIBITION_INVALID_REQUIREMENTS_LENGTH")
    private String requirements;
}
