package com.ssafy.yeobaek.analytics.service;

import com.ssafy.yeobaek.analytics.dto.request.RecordEventRequest;

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
}
