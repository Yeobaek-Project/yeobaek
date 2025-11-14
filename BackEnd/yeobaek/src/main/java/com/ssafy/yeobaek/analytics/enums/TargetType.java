package com.ssafy.yeobaek.analytics.enums;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 이벤트 대상 타입
 */
@Schema(description = "이벤트 대상 타입")
@Getter
@RequiredArgsConstructor
public enum TargetType {

    CAFE("카페"),
    ARTWORK("작품");

    @Schema(description = "대상 타입 설명")
    private final String description;
}
