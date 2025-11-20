package com.ssafy.yeobaek.analytics.service;

import com.ssafy.yeobaek.analytics.dto.request.RecordEventRequest;
import com.ssafy.yeobaek.analytics.entity.UserBehaviorEvent;
import com.ssafy.yeobaek.analytics.enums.EventType;
import com.ssafy.yeobaek.analytics.enums.TargetType;
import com.ssafy.yeobaek.analytics.repository.UserBehaviorEventRepository;
import com.ssafy.yeobaek.common.enums.UserRole;
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
        try {
            log.info("사용자 행동 이벤트 기록 시작 - 사용자 ID: {}, 이벤트 타입: {}, 대상 타입: {}, 대상 ID: {}",
                    userId, request.getEventType(), request.getTargetType(), request.getTargetId());
            // 사용자 조회
            User user = userService.findById(userId)
                    .orElseThrow(() -> {
                        log.warn("사용자 행동 이벤트 기록 실패 - 존재하지 않는 사용자 ID: {}", userId);
                        return new CustomException(ResponseCode.PROFILE_USER_NOT_FOUND);
                    });

            // String을 Enum으로 변환
            EventType eventType = EventType.valueOf(request.getEventType());
            TargetType targetType = TargetType.valueOf(request.getTargetType());

            // 사용자 유형에 따른 이벤트 수집 제한 검증
            validateUserTypeAndTargetType(user.getUserType(), targetType);

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
            log.info("사용자 행동 이벤트 기록 완료 - 이벤트 ID: {}", event.getId());
        } catch (Exception e) {
            log.error("사용자 행동 이벤트 기록 중 오류 발생 - 사용자 ID: {}, 요청: {}: {}", userId, request, e.getMessage());
        }
    }

    /**
     * 사용자 유형에 따른 타겟 타입 검증
     * - 카페 사장은 CAFE 이벤트 수집 불가
     * - 작가는 ARTWORK 이벤트 수집 불가
     */
    private void validateUserTypeAndTargetType(UserRole userRole, TargetType targetType) {
        if (userRole == UserRole.CAFE_OWNER && targetType == TargetType.CAFE) {
            log.warn("유효하지 않은 이벤트 수집 시도 - 사용자 유형: {}, 대상 타입: {}", userRole, targetType);
            throw new CustomException(ResponseCode.INVALID_INPUT_VALUE);
        }
        if (userRole == UserRole.ARTIST && targetType == TargetType.ARTWORK) {
            log.warn("유효하지 않은 이벤트 수집 시도 - 사용자 유형: {}, 대상 타입: {}", userRole, targetType);
            throw new CustomException(ResponseCode.INVALID_INPUT_VALUE);
        }
    }

    /**
     * 북마크 이벤트 기록 (서버 내부용)
     */
    @Override
    @Transactional
    public void recordBookmarkEvent(Long userId, Long targetId, TargetType targetType) {
        try {
            log.info("북마크 이벤트 기록 시작 - 사용자 ID: {}, 대상 ID: {}, 대상 타입: {}", userId, targetId, targetType);
            // 사용자 조회
            User user = userService.findById(userId)
                    .orElseThrow(() -> {
                        log.warn("북마크 이벤트 기록 실패 - 존재하지 않는 사용자 ID: {}", userId);
                        return new CustomException(ResponseCode.PROFILE_USER_NOT_FOUND);
                    });

            // 사용자 유형에 따른 이벤트 수집 제한 검증
            validateUserTypeAndTargetType(user.getUserType(), targetType);

            // 이벤트 엔티티 생성 및 저장
            UserBehaviorEvent event = UserBehaviorEvent.builder()
                    .user(user)
                    .eventType(EventType.BOOKMARK)
                    .targetType(targetType)
                    .targetId(targetId)
                    .build();

            userBehaviorEventRepository.save(event);
            log.info("북마크 이벤트 기록 완료 - 이벤트 ID: {}", event.getId());
        } catch (Exception e) {
            log.error("북마크 이벤트 기록 중 오류 발생 - 사용자 ID: {}, 대상 ID: {}, 대상 타입: {}: {}", userId, targetId, targetType, e.getMessage());
        }
    }

    /**
     * 이벤트 타입에 따른 필드 검증
     */
    private void validateEventFields(EventType eventType, RecordEventRequest request) {
        switch (eventType) {
            case DWELL:
                if (request.getDurationSeconds() == null) {
                    log.warn("DWELL 이벤트 필드 오류 - durationSeconds가 null입니다.");
                    throw new CustomException(ResponseCode.INVALID_INPUT_VALUE);
                }
                if (request.getMaxScrollPercent() != null) {
                    log.warn("DWELL 이벤트 필드 오류 - maxScrollPercent는 null이어야 합니다.");
                    throw new CustomException(ResponseCode.INVALID_INPUT_VALUE);
                }
                break;

            case SCROLL:
                if (request.getMaxScrollPercent() == null) {
                    log.warn("SCROLL 이벤트 필드 오류 - maxScrollPercent가 null입니다.");
                    throw new CustomException(ResponseCode.INVALID_INPUT_VALUE);
                }
                if (request.getDurationSeconds() != null) {
                    log.warn("SCROLL 이벤트 필드 오류 - durationSeconds는 null이어야 합니다.");
                    throw new CustomException(ResponseCode.INVALID_INPUT_VALUE);
                }
                break;

            case VIEW:
            case CHAT:
            case BOOKMARK:
                if (request.getDurationSeconds() != null || request.getMaxScrollPercent() != null) {
                    log.warn("{} 이벤트 필드 오류 - durationSeconds와 maxScrollPercent는 null이어야 합니다.", eventType);
                    throw new CustomException(ResponseCode.INVALID_INPUT_VALUE);
                }
                break;
        }
    }
}
