package com.ssafy.yeobaek.review.dto.response;

import com.ssafy.yeobaek.common.dto.Pagination;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Schema(description = "평가 목록 응답")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewListResponse {

    @Schema(description = "평가 목록")
    private List<ReviewDetail> reviews;

    @Schema(description = "페이징 정보")
    private Pagination pagination;
}
