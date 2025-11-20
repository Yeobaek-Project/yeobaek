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
        log.info("작품 추천 모델 학습 스케줄러를 시작합니다.");

        try {
            // 1. 암묵적 피드백 점수 계산
            List<ImplicitFeedbackScore> feedbackScores = implicitFeedbackService.calculateArtworkImplicitScores();
            log.info("작품에 대한 암묵적 피드백 점수 {}개를 계산했습니다.", feedbackScores.size());

            if (feedbackScores.isEmpty()) {
                log.warn("작품 모델 학습을 위한 피드백 데이터가 없어 스케줄러를 건너뜁니다.");
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
            log.info("작품 추천 모델 학습을 성공적으로 완료했습니다.");

        } catch (Exception e) {
            log.error("작품 추천 모델 학습 중 오류가 발생했습니다.", e);
        }
    }

    /**
     * 카페 추천 모델 학습 스케줄러
     * - 하루 4번 실행: 00:00, 06:00, 12:00, 18:00
     * - 사용자 행동 데이터를 암묵적 피드백으로 변환하여 ALS 모델 학습
     */
    @Scheduled(cron = "0 0 0,6,12,18 * * *")
    public void trainCafeRecommendationModel() {
        log.info("카페 추천 모델 학습 스케줄러를 시작합니다.");

        try {
            // 1. 암묵적 피드백 점수 계산
            List<ImplicitFeedbackScore> feedbackScores = implicitFeedbackService.calculateCafeImplicitScores();
            log.info("카페에 대한 암묵적 피드백 점수 {}개를 계산했습니다.", feedbackScores.size());

            if (feedbackScores.isEmpty()) {
                log.warn("카페 모델 학습을 위한 피드백 데이터가 없어 스케줄러를 건너뜁니다.");
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
            log.info("카페 추천 모델 학습을 성공적으로 완료했습니다.");

        } catch (Exception e) {
            log.error("카페 추천 모델 학습 중 오류가 발생했습니다.", e);
        }
    }
}
