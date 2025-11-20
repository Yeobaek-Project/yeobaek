package com.ssafy.yeobaek.analytics.dto.request;

import com.ssafy.yeobaek.analytics.enums.EventType;
import com.ssafy.yeobaek.analytics.enums.TargetType;
import com.ssafy.yeobaek.common.validation.ValidEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 사용자 행동 이벤트 기록 요청 DTO
 */
@Schema(description = "사용자 행동 이벤트 기록 요청")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RecordEventRequest {

    @NotNull(message = "MISSING_REQUIRED_FIELD")
    @ValidEnum(enumClass = EventType.class, message = "INVALID_INPUT_VALUE")
    @Schema(description = "이벤트 타입 (VIEW, DWELL, SCROLL, CHAT)", example = "VIEW", required = true)
    private String eventType;

    @NotNull(message = "MISSING_REQUIRED_FIELD")
    @ValidEnum(enumClass = TargetType.class, message = "INVALID_INPUT_VALUE")
    @Schema(description = "대상 타입", example = "CAFE", required = true)
    private String targetType;

    @NotNull(message = "MISSING_REQUIRED_FIELD")
    @Schema(description = "대상 ID (카페 ID 또는 작품 ID)", example = "123", required = true)
    private Long targetId;

    @Min(value = 0, message = "INVALID_INPUT_VALUE")
    @Schema(description = "체류 시간 (초) - DWELL 이벤트에만 사용", example = "45")
    private Integer durationSeconds;

    @Min(value = 0, message = "INVALID_INPUT_VALUE")
    @Max(value = 100, message = "INVALID_INPUT_VALUE")
    @Schema(description = "최대 스크롤 깊이 (0-100%) - SCROLL 이벤트에만 사용", example = "85")
    private Integer maxScrollPercent;
}
