package com.ssafy.yeobaek.analytics.entity;

import com.ssafy.yeobaek.analytics.enums.EventType;
import com.ssafy.yeobaek.analytics.enums.TargetType;
import com.ssafy.yeobaek.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * 사용자 행동 이벤트 엔티티
 * Collaborative Filtering을 위한 사용자 행동 데이터 저장
 */
@Entity
@Table(name = "user_behavior_event",
        indexes = {
                @Index(name = "idx_user_created", columnList = "user_id, created_at"),
                @Index(name = "idx_target", columnList = "target_type, target_id, created_at"),
                @Index(name = "idx_event_type", columnList = "event_type, created_at")
        })
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserBehaviorEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false, length = 50)
    private EventType eventType;

    @Enumerated(EnumType.STRING)
    @Column(name = "target_type", nullable = false, length = 20)
    private TargetType targetType;

    @Column(name = "target_id")
    private Long targetId;

    @Column(name = "duration_seconds")
    private Integer durationSeconds;

    @Column(name = "max_scroll_percent")
    private Integer maxScrollPercent;

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;
}
