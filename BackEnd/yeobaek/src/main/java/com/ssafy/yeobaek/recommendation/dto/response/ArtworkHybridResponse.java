package com.ssafy.yeobaek.recommendation.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ArtworkHybridResponse {
    private List<ArtworkItem> recommendations;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ArtworkItem {
        private Long artworkId;
        private Double score;
    }
}
