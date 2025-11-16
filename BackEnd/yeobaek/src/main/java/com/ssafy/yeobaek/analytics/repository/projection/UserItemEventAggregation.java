package com.ssafy.yeobaek.analytics.repository.projection;

/**
 * 사용자-아이템별 이벤트 집계 결과 Projection
 */
public interface UserItemEventAggregation {

    /**
     * 사용자 ID
     */
    Long getUserId();

    /**
     * 대상 ID (카페 또는 작품)
     */
    Long getTargetId();

    /**
     * VIEW 이벤트 횟수
     */
    Long getViewCount();

    /**
     * DWELL 이벤트의 최대 체류 시간 (초)
     */
    Integer getMaxDwellSeconds();

    /**
     * DWELL 이벤트 횟수
     */
    Long getDwellCount();

    /**
     * SCROLL 이벤트의 최대 스크롤 깊이 (%)
     */
    Integer getMaxScrollPercent();

    /**
     * SCROLL 이벤트 횟수
     */
    Long getScrollCount();

    /**
     * BOOKMARK 이벤트 존재 여부
     */
    Long getBookmarkCount();

    /**
     * CHAT 이벤트 존재 여부
     */
    Long getChatCount();
}
