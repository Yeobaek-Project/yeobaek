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
        log.info("Training artwork recommendation model with {} ratings", ratings.size());

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
                    .doOnSuccess(response -> log.info("Artwork model training completed: {}", response.getMessage()))
                    .doOnError(error -> log.error("Failed to train artwork model", error))
                    .block();
        } catch (Exception e) {
            log.error("Error training artwork model", e);
            throw new RuntimeException("Failed to train artwork recommendation model", e);
        }
    }

    @Override
    public void trainCafeModel(List<RatingData> ratings) {
        log.info("Training cafe recommendation model with {} ratings", ratings.size());

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
                    .doOnSuccess(response -> log.info("Cafe model training completed: {}", response.getMessage()))
                    .doOnError(error -> log.error("Failed to train cafe model", error))
                    .block();
        } catch (Exception e) {
            log.error("Error training cafe model", e);
            throw new RuntimeException("Failed to train cafe recommendation model", e);
        }
    }

    @Override
    public EmbeddingResponse generateEmbedding(String text) {
        log.debug("Generating embedding for text (length: {})", text.length());

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
                    .doOnError(error -> log.error("Failed to generate embedding", error))
                    .block();
        } catch (Exception e) {
            log.error("Error generating embedding", e);
            throw new RuntimeException("Failed to generate text embedding", e);
        }
    }

    @Override
    public BatchEmbeddingResponse generateEmbeddingsBatch(List<String> texts) {
        log.info("Generating batch embeddings for {} texts", texts.size());

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
                    .doOnSuccess(response -> log.info("Batch embeddings generated for {} texts", texts.size()))
                    .doOnError(error -> log.error("Failed to generate batch embeddings", error))
                    .block();
        } catch (Exception e) {
            log.error("Error generating batch embeddings", e);
            throw new RuntimeException("Failed to generate batch embeddings", e);
        }
    }

    @Override
    public ArtworkHybridResponse recommendArtworksForSlot(Long slotId, Integer topN) {
        log.info("Requesting artwork recommendations for slot {} (top {})", slotId, topN);

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
                    .doOnSuccess(response -> log.info("Received {} artwork recommendations", response.getRecommendations().size()))
                    .doOnError(error -> log.error("Failed to get artwork recommendations", error))
                    .block();
        } catch (Exception e) {
            log.error("Error getting artwork recommendations for slot {}", slotId, e);
            throw new RuntimeException("Failed to get artwork recommendations", e);
        }
    }

    @Override
    public CafeHybridResponse recommendCafesForArtwork(Long artworkId, Integer topN) {
        log.info("Requesting cafe recommendations for artwork {} (top {})", artworkId, topN);

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
                    .doOnSuccess(response -> log.info("Received {} cafe recommendations", response.getRecommendations().size()))
                    .doOnError(error -> log.error("Failed to get cafe recommendations", error))
                    .block();
        } catch (Exception e) {
            log.error("Error getting cafe recommendations for artwork {}", artworkId, e);
            throw new RuntimeException("Failed to get cafe recommendations", e);
        }
    }
}
