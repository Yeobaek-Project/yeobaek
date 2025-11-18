package com.ssafy.yeobaek.recommendation.service;

import com.ssafy.yeobaek.recommendation.dto.request.*;
import com.ssafy.yeobaek.recommendation.dto.response.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecommendationServiceImpl implements RecommendationService {

    private final WebClient pythonWebClient;

    @Value("${python.server.retry.max-attempts}")
    private int maxRetryAttempts;

    @Override
    public void trainArtworkModel(List<RatingData> ratings) {
        log.info("작품 추천 모델 학습을 시작합니다. 평점 데이터 수: {}", ratings.size());

        TrainRequest request = TrainRequest.builder()
                .ratings(ratings)
                .build();

        try {
            pythonWebClient.post()
                    .uri("/train/artworks")
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(TrainResponse.class)
                    .retryWhen(Retry.backoff(maxRetryAttempts, Duration.ofSeconds(1)))
                    .doOnSuccess(response -> log.info("작품 모델 학습이 완료되었습니다: {}", response.getMessage()))
                    .doOnError(error -> log.error("작품 모델 학습에 실패했습니다.", error))
                    .block();
        } catch (Exception e) {
            log.error("작품 모델 학습 중 오류가 발생했습니다.", e);
            throw new RuntimeException("작품 추천 모델 학습에 실패했습니다.", e);
        }
    }

    @Override
    public void trainCafeModel(List<RatingData> ratings) {
        log.info("카페 추천 모델 학습을 시작합니다. 평점 데이터 수: {}", ratings.size());

        TrainRequest request = TrainRequest.builder()
                .ratings(ratings)
                .build();

        try {
            pythonWebClient.post()
                    .uri("/train/cafes")
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(TrainResponse.class)
                    .retryWhen(Retry.backoff(maxRetryAttempts, Duration.ofSeconds(1)))
                    .doOnSuccess(response -> log.info("카페 모델 학습이 완료되었습니다: {}", response.getMessage()))
                    .doOnError(error -> log.error("카페 모델 학습에 실패했습니다.", error))
                    .block();
        } catch (Exception e) {
            log.error("카페 모델 학습 중 오류가 발생했습니다.", e);
            throw new RuntimeException("카페 추천 모델 학습에 실패했습니다.", e);
        }
    }

    @Override
    public EmbeddingResponse generateEmbedding(String text) {
        log.debug("텍스트 임베딩 생성을 시작합니다. (텍스트 길이: {})", text.length());

        EmbeddingRequest request = EmbeddingRequest.builder()
                .text(text)
                .build();

        try {
            return pythonWebClient.post()
                    .uri("/embeddings/generate")
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(EmbeddingResponse.class)
                    .retryWhen(Retry.backoff(maxRetryAttempts, Duration.ofSeconds(1)))
                    .doOnError(error -> log.error("임베딩 생성에 실패했습니다.", error))
                    .block();
        } catch (Exception e) {
            log.error("임베딩 생성 중 오류가 발생했습니다.", e);
            throw new RuntimeException("텍스트 임베딩 생성에 실패했습니다.", e);
        }
    }

    @Override
    public BatchEmbeddingResponse generateEmbeddingsBatch(List<String> texts) {
        log.info("배치 임베딩 생성을 시작합니다. 텍스트 수: {}", texts.size());

        BatchEmbeddingRequest request = BatchEmbeddingRequest.builder()
                .texts(texts)
                .build();

        try {
            return pythonWebClient.post()
                    .uri("/embeddings/batch")
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(BatchEmbeddingResponse.class)
                    .retryWhen(Retry.backoff(maxRetryAttempts, Duration.ofSeconds(1)))
                    .doOnSuccess(response -> log.info("텍스트 {}개에 대한 배치 임베딩이 생성되었습니다.", texts.size()))
                    .doOnError(error -> log.error("배치 임베딩 생성에 실패했습니다.", error))
                    .block();
        } catch (Exception e) {
            log.error("배치 임베딩 생성 중 오류가 발생했습니다.", e);
            throw new RuntimeException("배치 임베딩 생성에 실패했습니다.", e);
        }
    }

    @Override
    public ArtworkHybridResponse recommendArtworksForSlot(Long slotId, Integer topN) {
        log.info("슬롯 ID {}에 대한 작품 추천을 요청합니다. (상위 {}개)", slotId, topN);

        ArtworkRecommendRequest request = ArtworkRecommendRequest.builder()
                .slotId(slotId)
                .topN(topN != null ? topN : 10)
                .build();

        try {
            return pythonWebClient.post()
                    .uri("/recommend/artworks-for-slot")
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(ArtworkHybridResponse.class)
                    .retryWhen(Retry.backoff(maxRetryAttempts, Duration.ofSeconds(1)))
                    .doOnSuccess(response -> log.info("작품 추천을 {}개 받았습니다.", response.getRecommendations().size()))
                    .doOnError(error -> log.error("작품 추천을 받아오는데 실패했습니다.", error))
                    .block();
        } catch (Exception e) {
            log.error("슬롯 ID {}에 대한 작품 추천 중 오류가 발생했습니다.", slotId, e);
            throw new RuntimeException("작품 추천을 받아오는데 실패했습니다.", e);
        }
    }

    @Override
    public CafeHybridResponse recommendCafesForArtwork(Long artworkId, Integer topN) {
        log.info("작품 ID {}에 대한 카페 추천을 요청합니다. (상위 {}개)", artworkId, topN);

        CafeRecommendRequest request = CafeRecommendRequest.builder()
                .artworkId(artworkId)
                .topN(topN != null ? topN : 10)
                .build();

        try {
            return pythonWebClient.post()
                    .uri("/recommend/cafes-for-artwork")
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(CafeHybridResponse.class)
                    .retryWhen(Retry.backoff(maxRetryAttempts, Duration.ofSeconds(1)))
                    .doOnSuccess(response -> log.info("카페 추천을 {}개 받았습니다.", response.getRecommendations().size()))
                    .doOnError(error -> log.error("카페 추천을 받아오는데 실패했습니다.", error))
                    .block();
        } catch (Exception e) {
            log.error("작품 ID {}에 대한 카페 추천 중 오류가 발생했습니다.", artworkId, e);
            throw new RuntimeException("카페 추천을 받아오는데 실패했습니다.", e);
        }
    }
}
