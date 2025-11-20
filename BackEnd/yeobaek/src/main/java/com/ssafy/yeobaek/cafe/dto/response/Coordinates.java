package com.ssafy.yeobaek.cafe.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Coordinates {
    private double longitude; // 경도 (x)
    private double latitude;  // 위도 (y)
}
