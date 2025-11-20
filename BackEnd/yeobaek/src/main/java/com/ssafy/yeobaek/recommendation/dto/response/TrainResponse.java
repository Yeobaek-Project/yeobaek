package com.ssafy.yeobaek.recommendation.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TrainResponse {
    private String status;
    private String message;
    private Integer userCount;
    private Integer itemCount;
}
