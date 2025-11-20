package com.ssafy.yeobaek.cafe.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Schema(description = "작품 전시 가능일 응답 데이터")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SlotAvailableDatesResponse {
    @Schema(description = "전시 가능한 날짜 목록 (yyyy-MM-dd 형식)", example = "[\"2025-11-14\", \"2025-11-15\", \"2025-11-16\"]")
    private List<LocalDate> dates;
}
