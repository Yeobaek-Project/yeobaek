package com.ssafy.yeobaek.recommendation.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssafy.yeobaek.artwork.entity.Artwork;
import com.ssafy.yeobaek.artwork.repository.ArtworkRepository;
import com.ssafy.yeobaek.cafe.entity.DisplaySlot;
import com.ssafy.yeobaek.cafe.repository.DisplaySlotRepository;
import com.ssafy.yeobaek.recommendation.dto.response.BatchEmbeddingResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmbeddingBatchService {

    private final RecommendationService recommendationService;
    private final ArtworkRepository artworkRepository;
    private final DisplaySlotRepository displaySlotRepository;
    private final ObjectMapper objectMapper;

    /**
     * 임베딩이 없는 모든 Artwork에 대해 배치로 임베딩 생성 및 저장
     *
     * @return 처리된 작품 수
     */
    @Transactional
    public int batchGenerateArtworkEmbeddings() {
        log.info("Starting batch embedding generation for artworks");

        // 임베딩이 없고 description이 있는 Artwork 조회
        List<Artwork> artworksWithoutEmbedding = artworkRepository.findAll().stream()
                .filter(artwork -> (artwork.getDescriptionEmbedding() == null || artwork.getDescriptionEmbedding().isEmpty())
                        && artwork.getDescription() != null
                        && !artwork.getDescription().trim().isEmpty())
                .collect(Collectors.toList());

        if (artworksWithoutEmbedding.isEmpty()) {
            log.info("No artworks need embedding generation");
            return 0;
        }

        log.info("Found {} artworks without embeddings", artworksWithoutEmbedding.size());

        try {
            // 모든 description 수집
            List<String> descriptions = artworksWithoutEmbedding.stream()
                    .map(Artwork::getDescription)
                    .collect(Collectors.toList());

            // 배치로 임베딩 생성
            BatchEmbeddingResponse response = recommendationService.generateEmbeddingsBatch(descriptions);
            List<List<Double>> embeddings = response.getEmbeddings();

            // 각 Artwork에 임베딩 저장
            for (int i = 0; i < artworksWithoutEmbedding.size(); i++) {
                Artwork artwork = artworksWithoutEmbedding.get(i);
                List<Double> embedding = embeddings.get(i);
                String embeddingJson = convertEmbeddingToJson(embedding);
                artwork.setDescriptionEmbedding(embeddingJson);
            }

            artworkRepository.saveAll(artworksWithoutEmbedding);

            log.info("Successfully generated embeddings for {} artworks", artworksWithoutEmbedding.size());
            return artworksWithoutEmbedding.size();

        } catch (Exception e) {
            log.error("Failed to batch generate artwork embeddings", e);
            throw new RuntimeException("Failed to batch generate artwork embeddings", e);
        }
    }

    /**
     * 임베딩이 없는 모든 DisplaySlot에 대해 배치로 임베딩 생성 및 저장
     *
     * @return 처리된 슬롯 수
     */
    @Transactional
    public int batchGenerateSlotEmbeddings() {
        log.info("Starting batch embedding generation for display slots");

        // 임베딩이 없고 requirements가 있는 DisplaySlot 조회
        List<DisplaySlot> slotsWithoutEmbedding = displaySlotRepository.findAll().stream()
                .filter(slot -> (slot.getRequirementsEmbedding() == null || slot.getRequirementsEmbedding().isEmpty())
                        && slot.getRequirements() != null
                        && !slot.getRequirements().trim().isEmpty())
                .collect(Collectors.toList());

        if (slotsWithoutEmbedding.isEmpty()) {
            log.info("No display slots need embedding generation");
            return 0;
        }

        log.info("Found {} display slots without embeddings", slotsWithoutEmbedding.size());

        try {
            // 모든 requirements 수집
            List<String> requirementsList = slotsWithoutEmbedding.stream()
                    .map(DisplaySlot::getRequirements)
                    .collect(Collectors.toList());

            // 배치로 임베딩 생성
            BatchEmbeddingResponse response = recommendationService.generateEmbeddingsBatch(requirementsList);
            List<List<Double>> embeddings = response.getEmbeddings();

            // 각 DisplaySlot에 임베딩 저장
            for (int i = 0; i < slotsWithoutEmbedding.size(); i++) {
                DisplaySlot slot = slotsWithoutEmbedding.get(i);
                List<Double> embedding = embeddings.get(i);
                String embeddingJson = convertEmbeddingToJson(embedding);
                slot.setRequirementsEmbedding(embeddingJson);
            }

            displaySlotRepository.saveAll(slotsWithoutEmbedding);

            log.info("Successfully generated embeddings for {} display slots", slotsWithoutEmbedding.size());
            return slotsWithoutEmbedding.size();

        } catch (Exception e) {
            log.error("Failed to batch generate slot embeddings", e);
            throw new RuntimeException("Failed to batch generate slot embeddings", e);
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
            log.error("Failed to convert embedding to JSON", e);
            throw new RuntimeException("Failed to convert embedding to JSON", e);
        }
    }
}
