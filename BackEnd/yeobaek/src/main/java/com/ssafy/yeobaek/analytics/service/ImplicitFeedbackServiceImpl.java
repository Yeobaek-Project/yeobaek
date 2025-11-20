package com.ssafy.yeobaek.analytics.service;

import com.ssafy.yeobaek.analytics.config.ImplicitFeedbackConfig;
import com.ssafy.yeobaek.analytics.dto.response.ImplicitFeedbackScore;
import com.ssafy.yeobaek.analytics.enums.TargetType;
import com.ssafy.yeobaek.analytics.repository.UserBehaviorEventRepository;
import com.ssafy.yeobaek.analytics.repository.projection.UserItemEventAggregation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 암묵적 피드백 점수 계산 서비스 구현체
 */
@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ImplicitFeedbackServiceImpl implements ImplicitFeedbackService {

    private final UserBehaviorEventRepository userBehaviorEventRepository;

    @Override
    public List<ImplicitFeedbackScore> calculateCafeImplicitScores() {
        log.info("카페에 대한 암묵적 피드백 점수 계산 시작");
        List<UserItemEventAggregation> aggregations =
            userBehaviorEventRepository.aggregateEventsByTargetType(TargetType.CAFE.name());

        List<ImplicitFeedbackScore> scores = aggregations.stream()
                .map(this::calculateScore)
                .collect(Collectors.toList());

        log.info("카페 암묵적 피드백 점수 계산 완료: {} 건", scores.size());
        return scores;
    }

    @Override
    public List<ImplicitFeedbackScore> calculateArtworkImplicitScores() {
        log.info("작품에 대한 암묵적 피드백 점수 계산 시작");
        List<UserItemEventAggregation> aggregations =
            userBehaviorEventRepository.aggregateEventsByTargetType(TargetType.ARTWORK.name());

        List<ImplicitFeedbackScore> scores = aggregations.stream()
                .map(this::calculateScore)
                .collect(Collectors.toList());

        log.info("작품 암묵적 피드백 점수 계산 완료: {} 건", scores.size());
        return scores;
    }

    /**
     * 집계 데이터로부터 암묵적 피드백 점수 계산
     */
    private ImplicitFeedbackScore calculateScore(UserItemEventAggregation agg) {
        double score = 0.0;

        // VIEW 점수: 1.0 × log(1 + count)
        if (agg.getViewCount() > 0) {
            double viewScore = ImplicitFeedbackConfig.VIEW_WEIGHT *
                ImplicitFeedbackConfig.calculateFrequencyWeight(agg.getViewCount());
            score += viewScore;
        }

        // DWELL 점수: min(3.0, 0.05 × maxSeconds) × log(1 + count)
        if (agg.getDwellCount() > 0 && agg.getMaxDwellSeconds() != null) {
            double dwellBaseScore = ImplicitFeedbackConfig.calculateDwellScore(agg.getMaxDwellSeconds());
            double dwellScore = dwellBaseScore *
                ImplicitFeedbackConfig.calculateFrequencyWeight(agg.getDwellCount());
            score += dwellScore;
        }

        // SCROLL 점수: 0.04 × maxPercent × log(1 + count)
        if (agg.getScrollCount() > 0 && agg.getMaxScrollPercent() != null) {
            double scrollBaseScore = ImplicitFeedbackConfig.calculateScrollScore(agg.getMaxScrollPercent());
            double scrollScore = scrollBaseScore *
                ImplicitFeedbackConfig.calculateFrequencyWeight(agg.getScrollCount());
            score += scrollScore;
        }

        // BOOKMARK 점수: 7.0 (존재 시)
        if (agg.getBookmarkCount() > 0) {
            score += ImplicitFeedbackConfig.BOOKMARK_WEIGHT;
        }

        // CHAT 점수: 10.0 (존재 시)
        if (agg.getChatCount() > 0) {
            score += ImplicitFeedbackConfig.CHAT_WEIGHT;
        }

        return ImplicitFeedbackScore.builder()
                .userId(agg.getUserId())
                .itemId(agg.getTargetId())
                .score(score)
                .build();
    }
}
