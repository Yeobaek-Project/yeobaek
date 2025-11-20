package com.ssafy.yeobaek.recommendation.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssafy.yeobaek.artwork.entity.Artwork;
import com.ssafy.yeobaek.artwork.repository.ArtworkRepository;
import com.ssafy.yeobaek.cafe.entity.DisplaySlot;
import com.ssafy.yeobaek.cafe.repository.DisplaySlotRepository;
import com.ssafy.yeobaek.recommendation.dto.response.EmbeddingResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmbeddingService {

    private final RecommendationService recommendationService;
    private final ArtworkRepository artworkRepository;
    private final DisplaySlotRepository displaySlotRepository;
    private final ObjectMapper objectMapper;

    /**
     * Artwork 생성 후 비동기로 description 임베딩 생성 및 저장
     *
     * @param artworkId 작품 ID
     */
    @Async("recommendationTaskExecutor")
    @Transactional
    public void generateAndSaveArtworkEmbedding(Long artworkId) {
        log.info("작품 ID {}에 대한 비동기 임베딩 생성을 시작합니다.", artworkId);

        try {
            // Artwork 조회
            Artwork artwork = artworkRepository.findById(artworkId)
                    .orElseThrow(() -> new IllegalArgumentException("작품을 찾을 수 없습니다: " + artworkId));

            // 이미 임베딩이 있으면 스킵
            if (artwork.getDescriptionEmbedding() != null && !artwork.getDescriptionEmbedding().isEmpty()) {
                log.info("작품 ID {}에 이미 임베딩이 존재하므로 건너뜁니다.", artworkId);
                return;
            }

            // description이 없으면 스킵
            if (artwork.getDescription() == null || artwork.getDescription().trim().isEmpty()) {
                log.warn("작품 ID {}에 설명이 없어 임베딩 생성을 건너뜁니다.", artworkId);
                return;
            }

            // 파이썬 서버로 임베딩 생성 요청
            EmbeddingResponse response = recommendationService.generateEmbedding(artwork.getDescription());

            // 임베딩을 JSON 문자열로 변환하여 저장
            String embeddingJson = convertEmbeddingToJson(response.getEmbedding());
            artwork.setDescriptionEmbedding(embeddingJson);
            artworkRepository.save(artwork);

            log.info("작품 ID {}에 대한 임베딩을 성공적으로 생성하고 저장했습니다.", artworkId);

        } catch (Exception e) {
            log.error("작품 ID {}에 대한 임베딩 생성 중 오류가 발생했습니다.", artworkId, e);
            // 비동기이므로 예외를 던지지 않고 로그만 남김
        }
    }

    /**
     * DisplaySlot 생성 후 비동기로 requirements 임베딩 생성 및 저장
     *
     * @param slotId 슬롯 ID
     */
    @Async("recommendationTaskExecutor")
    @Transactional
    public void generateAndSaveSlotEmbedding(Long slotId) {
        log.info("슬롯 ID {}에 대한 비동기 임베딩 생성을 시작합니다.", slotId);

        try {
            // DisplaySlot 조회
            DisplaySlot slot = displaySlotRepository.findById(slotId)
                    .orElseThrow(() -> new IllegalArgumentException("전시 슬롯을 찾을 수 없습니다: " + slotId));

            // 이미 임베딩이 있으면 스킵
            if (slot.getRequirementsEmbedding() != null && !slot.getRequirementsEmbedding().isEmpty()) {
                log.info("슬롯 ID {}에 이미 임베딩이 존재하므로 건너뜁니다.", slotId);
                return;
            }

            // requirements가 없으면 스킵
            if (slot.getRequirements() == null || slot.getRequirements().trim().isEmpty()) {
                log.warn("슬롯 ID {}에 요구사항이 없어 임베딩 생성을 건너뜁니다.", slotId);
                return;
            }

            // 파이썬 서버로 임베딩 생성 요청
            EmbeddingResponse response = recommendationService.generateEmbedding(slot.getRequirements());

            // 임베딩을 JSON 문자열로 변환하여 저장
            String embeddingJson = convertEmbeddingToJson(response.getEmbedding());
            slot.setRequirementsEmbedding(embeddingJson);
            displaySlotRepository.save(slot);

            log.info("슬롯 ID {}에 대한 임베딩을 성공적으로 생성하고 저장했습니다.", slotId);

        } catch (Exception e) {
            log.error("슬롯 ID {}에 대한 임베딩 생성 중 오류가 발생했습니다.", slotId, e);
            // 비동기이므로 예외를 던지지 않고 로그만 남김
        }
    }

    /**
     * 임베딩 벡터를 JSON 문자열로 변환
     *
     * @param embedding 임베딩 벡터
     * @return JSON 문자열
     */
    private String convertEmbeddingToJson(List<Double> embedding) {
        try {
            return objectMapper.writeValueAsString(embedding);
        } catch (JsonProcessingException e) {
            log.error("임베딩을 JSON으로 변환하는 데 실패했습니다.", e);
            throw new RuntimeException("임베딩을 JSON으로 변환하는 데 실패했습니다.", e);
        }
    }
}
