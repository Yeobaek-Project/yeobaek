package com.ssafy.yeobaek.analytics.service;

import com.ssafy.yeobaek.analytics.dto.request.RecordEventRequest;
import com.ssafy.yeobaek.analytics.enums.TargetType;

/**
 * 사용자 행동 분석 서비스 인터페이스
 */
public interface AnalyticsService {

    /**
     * 사용자 행동 이벤트 기록
     *
     * @param userId 사용자 ID
     * @param request 이벤트 기록 요청
     */
    void recordEvent(Long userId, RecordEventRequest request);

    /**
     * 북마크 이벤트 기록 (서버 내부용)
     *
     * @param userId 사용자 ID
     * @param targetId 대상 ID (카페 또는 작품)
     * @param targetType 대상 타입
     */
    void recordBookmarkEvent(Long userId, Long targetId, TargetType targetType);
}
