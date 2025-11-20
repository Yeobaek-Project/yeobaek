package com.ssafy.yeobaek.artwork.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Schema(description = "작가 홈 응답 데이터")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ArtistHomeResponse {
    @Schema(description = "작가 정보")
    private ArtistDto artist;
    
    @Schema(description = "인기 작품 목록 (최대 3개)")
    private List<ArtworkSummaryDto> artworks;
}
