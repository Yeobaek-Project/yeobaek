package com.ssafy.yeobaek.artwork.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.ssafy.yeobaek.common.dto.Pagination;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Schema(description = "작품 목록 응답 데이터")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ArtworkListResponse {
    @Schema(description = "작가 목록 (검색 결과에만 포함)")
    private List<ArtistDto> artists;

    @Schema(description = "작품 목록")
    private List<ArtworkSummaryDto> artworks;

    @Schema(description = "페이징 정보")
    private Pagination pagination;
}
