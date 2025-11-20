package com.ssafy.yeobaek.cafe.dto.response;

import com.ssafy.yeobaek.common.dto.Pagination;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Schema(description = "카페 목록 응답 데이터")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CafeListData {
    @Schema(description = "카페 목록")
    private List<CafeSummary> cafes;

    @Schema(description = "페이징 정보")
    private Pagination pagination;
}
