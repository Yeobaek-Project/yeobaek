package com.ssafy.yeobaek.recommendation.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ArtworkRecommendRequest {
    @JsonProperty("slot_id")
    private Long slotId;

    @JsonProperty("top_n")
    @Builder.Default
    private Integer topN = 10;
}
