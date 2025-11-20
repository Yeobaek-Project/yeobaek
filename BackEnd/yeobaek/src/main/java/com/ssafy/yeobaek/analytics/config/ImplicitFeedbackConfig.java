package com.ssafy.yeobaek.analytics.config;

/**
 * 암묵적 피드백 점수 계산을 위한 가중치 설정
 */
public class ImplicitFeedbackConfig {

    // 기본 가중치
    public static final double VIEW_WEIGHT = 1.0;
    public static final double DWELL_WEIGHT_PER_SECOND = 0.05;
    public static final double SCROLL_WEIGHT_PER_PERCENT = 0.04;
    public static final double BOOKMARK_WEIGHT = 7.0;
    public static final double CHAT_WEIGHT = 10.0;

    // 체류 시간 최대 점수
    public static final double MAX_DWELL_SCORE = 3.0;
    public static final int MAX_DWELL_SECONDS = 60;

    // 스크롤 최대 점수
    public static final double MAX_SCROLL_SCORE = 4.0;
    public static final int MAX_SCROLL_PERCENT = 100;

    private ImplicitFeedbackConfig() {
        // 유틸리티 클래스이므로 인스턴스화 방지
    }

    /**
     * 체류 시간에 따른 점수 계산
     * @param seconds 체류 시간 (초)
     * @return 체류 점수 (최대 3.0)
     */
    public static double calculateDwellScore(int seconds) {
        return Math.min(MAX_DWELL_SCORE, seconds * DWELL_WEIGHT_PER_SECOND);
    }

    /**
     * 스크롤 깊이에 따른 점수 계산
     * @param percent 스크롤 깊이 (0-100)
     * @return 스크롤 점수 (최대 4.0)
     */
    public static double calculateScrollScore(int percent) {
        return percent * SCROLL_WEIGHT_PER_PERCENT;
    }

    /**
     * 빈도에 따른 로그 가중치 계산
     * @param count 행동 횟수
     * @return log(1 + count)
     */
    public static double calculateFrequencyWeight(long count) {
        return Math.log(1 + count);
    }
}
