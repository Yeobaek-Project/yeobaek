package com.ssafy.yeobaek.artwork.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Schema(description = "작품 전시 가능일 응답 데이터")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ArtworkAvailableDatesResponse {
    @Schema(description = "전시 가능한 날짜 목록 (yyyy-MM-dd 형식)", example = "[\"2025-11-14\", \"2025-11-15\", \"2025-11-16\"]")
    private List<LocalDate> dates;
}
