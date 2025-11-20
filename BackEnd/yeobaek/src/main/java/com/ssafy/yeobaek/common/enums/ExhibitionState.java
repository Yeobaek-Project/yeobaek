package com.ssafy.yeobaek.common.enums;

/**
 * Exhibition 엔티티의 상태를 나타내는 enum
 * (ExhibitionStatus는 Artwork/DisplaySlot의 전시 가능 여부를 나타냄)
 */
public enum ExhibitionState {
    PENDING,      // 제안 대기 (한쪽만 동의 또는 아무도 동의 안함)
    APPROVED,     // 양쪽 모두 동의 (전시 예정)
    ONGOING,      // 전시 진행 중
    COMPLETED,    // 전시 종료
    CANCELLED,    // 전시 취소
    REJECTED;     // 제안 거절

    /**
     * 전시가 진행 가능한 상태인지 확인
     */
    public boolean isActive() {
        return this == APPROVED || this == ONGOING;
    }

    /**
     * 전시가 종료된 상태인지 확인
     */
    public boolean isFinished() {
        return this == COMPLETED || this == CANCELLED || this == REJECTED;
    }

    /**
     * 전시를 취소할 수 있는 상태인지 확인
     */
    public boolean isCancellable() {
        return this == PENDING || this == APPROVED;
    }
}
