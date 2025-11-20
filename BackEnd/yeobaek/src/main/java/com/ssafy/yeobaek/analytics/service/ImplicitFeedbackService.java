package com.ssafy.yeobaek.analytics.service;

import com.ssafy.yeobaek.analytics.dto.response.ImplicitFeedbackScore;

import java.util.List;

/**
 * 암묵적 피드백 점수 계산 서비스
 */
public interface ImplicitFeedbackService {

    /**
     * 카페에 대한 암묵적 피드백 점수 계산
     *
     * @return 사용자-카페별 암묵적 피드백 점수 리스트
     */
    List<ImplicitFeedbackScore> calculateCafeImplicitScores();

    /**
     * 작품에 대한 암묵적 피드백 점수 계산
     *
     * @return 사용자-작품별 암묵적 피드백 점수 리스트
     */
    List<ImplicitFeedbackScore> calculateArtworkImplicitScores();
}
