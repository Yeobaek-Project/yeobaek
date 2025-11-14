package com.ssafy.yeobaek.analytics.service;

import com.ssafy.yeobaek.analytics.dto.request.RecordEventRequest;
import com.ssafy.yeobaek.analytics.entity.UserBehaviorEvent;
import com.ssafy.yeobaek.analytics.enums.EventType;
import com.ssafy.yeobaek.analytics.enums.TargetType;
import com.ssafy.yeobaek.analytics.repository.UserBehaviorEventRepository;
import com.ssafy.yeobaek.common.exception.CustomException;
import com.ssafy.yeobaek.common.code.ResponseCode;
import com.ssafy.yeobaek.user.entity.User;
import com.ssafy.yeobaek.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 사용자 행동 분석 서비스 구현체
 */
@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AnalyticsServiceImpl implements AnalyticsService {

    private final UserBehaviorEventRepository userBehaviorEventRepository;
    private final UserService userService;

    /**
     * 사용자 행동 이벤트 기록
     */
    @Override
    @Transactional
    public void recordEvent(Long userId, RecordEventRequest request) {
        // 사용자 조회
        User user = userService.findById(userId)
                .orElseThrow(() -> new CustomException(ResponseCode.PROFILE_USER_NOT_FOUND));

        // String을 Enum으로 변환
        EventType eventType = EventType.valueOf(request.getEventType());
        TargetType targetType = TargetType.valueOf(request.getTargetType());

        // 이벤트 타입별 필수 필드 검증
        validateEventFields(eventType, request);

        // 이벤트 엔티티 생성 및 저장
        UserBehaviorEvent event = UserBehaviorEvent.builder()
                .user(user)
                .eventType(eventType)
                .targetType(targetType)
                .targetId(request.getTargetId())
                .durationSeconds(request.getDurationSeconds())
                .maxScrollPercent(request.getMaxScrollPercent())
                .build();

        userBehaviorEventRepository.save(event);
    }

    /**
     * 이벤트 타입에 따른 필드 검증
     */
    private void validateEventFields(EventType eventType, RecordEventRequest request) {
        switch (eventType) {
            case DWELL:
                if (request.getDurationSeconds() == null) {
                    throw new CustomException(ResponseCode.INVALID_INPUT_VALUE);
                }
                if (request.getMaxScrollPercent() != null) {
                    throw new CustomException(ResponseCode.INVALID_INPUT_VALUE);
                }
                break;

            case SCROLL:
                if (request.getMaxScrollPercent() == null) {
                    throw new CustomException(ResponseCode.INVALID_INPUT_VALUE);
                }
                if (request.getDurationSeconds() != null) {
                    throw new CustomException(ResponseCode.INVALID_INPUT_VALUE);
                }
                break;

            case VIEW:
            case CHAT:
                if (request.getDurationSeconds() != null || request.getMaxScrollPercent() != null) {
                    throw new CustomException(ResponseCode.INVALID_INPUT_VALUE);
                }
                break;
        }
    }
}
