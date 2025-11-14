package com.ssafy.yeobaek.analytics.repository;

import com.ssafy.yeobaek.analytics.entity.UserBehaviorEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 사용자 행동 이벤트 Repository
 */
@Repository
public interface UserBehaviorEventRepository extends JpaRepository<UserBehaviorEvent, Long> {
}
