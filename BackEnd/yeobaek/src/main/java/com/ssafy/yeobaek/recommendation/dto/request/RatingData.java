package com.ssafy.yeobaek.recommendation.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RatingData {
    private Long userId;
    private Long itemId;
    private Double score;
}
