package com.ssafy.yeobaek.analytics.repository;

import com.ssafy.yeobaek.analytics.entity.UserBehaviorEvent;
import com.ssafy.yeobaek.analytics.repository.projection.UserItemEventAggregation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 사용자 행동 이벤트 Repository
 */
@Repository
public interface UserBehaviorEventRepository extends JpaRepository<UserBehaviorEvent, Long> {

    /**
     * 사용자-아이템별 이벤트 집계 (특정 TargetType)
     * VIEW, DWELL, SCROLL, BOOKMARK, CHAT 이벤트를 집계하여 암묵적 피드백 점수 계산에 사용
     *
     * @param targetType 대상 타입 (CAFE 또는 ARTWORK)
     * @return 사용자-아이템별 집계 결과 리스트
     */
    @Query(value = """
        SELECT
            ube.user_id AS userId,
            ube.target_id AS targetId,
            COALESCE(SUM(CASE WHEN ube.event_type = 'VIEW' THEN 1 ELSE 0 END), 0) AS viewCount,
            MAX(CASE WHEN ube.event_type = 'DWELL' THEN ube.duration_seconds END) AS maxDwellSeconds,
            COALESCE(SUM(CASE WHEN ube.event_type = 'DWELL' THEN 1 ELSE 0 END), 0) AS dwellCount,
            MAX(CASE WHEN ube.event_type = 'SCROLL' THEN ube.max_scroll_percent END) AS maxScrollPercent,
            COALESCE(SUM(CASE WHEN ube.event_type = 'SCROLL' THEN 1 ELSE 0 END), 0) AS scrollCount,
            COALESCE(SUM(CASE WHEN ube.event_type = 'BOOKMARK' THEN 1 ELSE 0 END), 0) AS bookmarkCount,
            COALESCE(SUM(CASE WHEN ube.event_type = 'CHAT' THEN 1 ELSE 0 END), 0) AS chatCount
        FROM user_behavior_event ube
        WHERE ube.target_type = :targetType
        GROUP BY ube.user_id, ube.target_id
        """, nativeQuery = true)
    List<UserItemEventAggregation> aggregateEventsByTargetType(@Param("targetType") String targetType);
}
