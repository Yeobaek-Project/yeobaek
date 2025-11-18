package com.ssafy.yeobaek.recommendation.scheduler;

import com.ssafy.yeobaek.analytics.dto.response.ImplicitFeedbackScore;
import com.ssafy.yeobaek.analytics.service.ImplicitFeedbackService;
import com.ssafy.yeobaek.recommendation.dto.request.RatingData;
import com.ssafy.yeobaek.recommendation.service.RecommendationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class RecommendationScheduler {

    private final ImplicitFeedbackService implicitFeedbackService;
    private final RecommendationService recommendationService;

    /**
     * 작품 추천 모델 학습 스케줄러
     * - 하루 4번 실행: 00:00, 06:00, 12:00, 18:00
     * - 사용자 행동 데이터를 암묵적 피드백으로 변환하여 ALS 모델 학습
     */
    @Scheduled(cron = "0 0 0,6,12,18 * * *")
    public void trainArtworkRecommendationModel() {
        log.info("Starting artwork recommendation model training...");

        try {
            // 1. 암묵적 피드백 점수 계산
            List<ImplicitFeedbackScore> feedbackScores = implicitFeedbackService.calculateArtworkImplicitScores();
            log.info("Calculated {} implicit feedback scores for artworks", feedbackScores.size());

            if (feedbackScores.isEmpty()) {
                log.warn("No feedback data available for artwork model training. Skipping...");
                return;
            }

            // 2. RatingData 형식으로 변환
            List<RatingData> ratings = feedbackScores.stream()
                    .map(score -> RatingData.builder()
                            .userId(score.getUserId())
                            .itemId(score.getItemId())
                            .score(score.getScore())
                            .build())
                    .collect(Collectors.toList());

            // 3. 파이썬 서버로 모델 학습 요청
            recommendationService.trainArtworkModel(ratings);
            log.info("Artwork recommendation model training completed successfully");

        } catch (Exception e) {
            log.error("Failed to train artwork recommendation model", e);
        }
    }

    /**
     * 카페 추천 모델 학습 스케줄러
     * - 하루 4번 실행: 00:00, 06:00, 12:00, 18:00
     * - 사용자 행동 데이터를 암묵적 피드백으로 변환하여 ALS 모델 학습
     */
    @Scheduled(cron = "0 0 0,6,12,18 * * *")
    public void trainCafeRecommendationModel() {
        log.info("Starting cafe recommendation model training...");

        try {
            // 1. 암묵적 피드백 점수 계산
            List<ImplicitFeedbackScore> feedbackScores = implicitFeedbackService.calculateCafeImplicitScores();
            log.info("Calculated {} implicit feedback scores for cafes", feedbackScores.size());

            if (feedbackScores.isEmpty()) {
                log.warn("No feedback data available for cafe model training. Skipping...");
                return;
            }

            // 2. RatingData 형식으로 변환
            List<RatingData> ratings = feedbackScores.stream()
                    .map(score -> RatingData.builder()
                            .userId(score.getUserId())
                            .itemId(score.getItemId())
                            .score(score.getScore())
                            .build())
                    .collect(Collectors.toList());

            // 3. 파이썬 서버로 모델 학습 요청
            recommendationService.trainCafeModel(ratings);
            log.info("Cafe recommendation model training completed successfully");

        } catch (Exception e) {
            log.error("Failed to train cafe recommendation model", e);
        }
    }
}
