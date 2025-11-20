package com.ssafy.yeobaek.cafe.dto.response;

import com.ssafy.yeobaek.common.dto.Pagination;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@Schema(description = "추천 카페 목록 응답 데이터")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CafeRecommendationResponse {

    @Schema(description = "추천 카페 목록")
    private List<CafeSummaryForRecommendation> cafes;

    @Schema(description = "페이징 정보")
    private Pagination pagination;
}
