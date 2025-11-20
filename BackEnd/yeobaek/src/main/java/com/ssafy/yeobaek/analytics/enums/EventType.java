package com.ssafy.yeobaek.analytics.enums;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 사용자 행동 이벤트 타입
 */
@Schema(description = "사용자 행동 이벤트 타입")
@Getter
@RequiredArgsConstructor
public enum EventType {

    // 조회 이벤트
    VIEW("상세 조회"),

    // 체류 시간 이벤트
    DWELL("페이지 체류"),

    // 스크롤 깊이 이벤트
    SCROLL("페이지 스크롤"),

    // 채팅 이벤트
    CHAT("채팅 문의 시작"),

    // 북마크 이벤트
    BOOKMARK("북마크 추가/삭제");

    @Schema(description = "이벤트 타입 설명")
    private final String description;
}
