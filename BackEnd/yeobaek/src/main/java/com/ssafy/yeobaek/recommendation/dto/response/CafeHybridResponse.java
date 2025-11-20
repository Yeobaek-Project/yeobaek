package com.ssafy.yeobaek.recommendation.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CafeHybridResponse {
    private List<CafeItem> recommendations;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CafeItem {
        private Long cafeId;
        private Double score;
    }
}
