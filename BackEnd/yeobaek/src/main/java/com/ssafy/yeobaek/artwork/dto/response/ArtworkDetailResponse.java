package com.ssafy.yeobaek.artwork.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Schema(description = "작품 상세 응답 데이터")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ArtworkDetailResponse {
    @Schema(description = "작품 상세 정보")
    private ArtworkDetailDto artwork;
}
