package com.ssafy.yeobaek.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Schema(description = "페이징 정보")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pagination {
    @Schema(description = "현재 페이지 번호", example = "0")
    private Integer currentPage;

    @Schema(description = "페이지 크기", example = "20")
    private Integer pageSize;

    @Schema(description = "전체 항목 수", example = "100")
    private Long totalElements;

    @Schema(description = "전체 페이지 수", example = "5")
    private Integer totalPages;

    @Schema(description = "다음 페이지 존재 여부", example = "true")
    private Boolean hasNext;
}
