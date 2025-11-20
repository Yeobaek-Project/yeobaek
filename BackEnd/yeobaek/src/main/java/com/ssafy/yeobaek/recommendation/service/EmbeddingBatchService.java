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
        log.info("작품에 대한 배치 임베딩 생성을 시작합니다.");

        // 임베딩이 없고 description이 있는 Artwork 조회
        List<Artwork> artworksWithoutEmbedding = artworkRepository.findAll().stream()
                .filter(artwork -> (artwork.getDescriptionEmbedding() == null || artwork.getDescriptionEmbedding().isEmpty())
                        && artwork.getDescription() != null
                        && !artwork.getDescription().trim().isEmpty())
                .collect(Collectors.toList());

        if (artworksWithoutEmbedding.isEmpty()) {
            log.info("임베딩 생성이 필요한 작품이 없습니다.");
            return 0;
        }

        log.info("임베딩이 없는 작품 {}개를 찾았습니다.", artworksWithoutEmbedding.size());

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

            log.info("작품 {}개에 대한 임베딩을 성공적으로 생성하고 저장했습니다.", artworksWithoutEmbedding.size());
            return artworksWithoutEmbedding.size();

        } catch (Exception e) {
            log.error("작품 배치 임베딩 생성 중 오류가 발생했습니다.", e);
            throw new RuntimeException("작품 배치 임베딩 생성에 실패했습니다.", e);
        }
    }

    /**
     * 임베딩이 없는 모든 DisplaySlot에 대해 배치로 임베딩 생성 및 저장
     *
     * @return 처리된 슬롯 수
     */
    @Transactional
    public int batchGenerateSlotEmbeddings() {
        log.info("전시 슬롯에 대한 배치 임베딩 생성을 시작합니다.");

        // 임베딩이 없고 requirements가 있는 DisplaySlot 조회
        List<DisplaySlot> slotsWithoutEmbedding = displaySlotRepository.findAll().stream()
                .filter(slot -> (slot.getRequirementsEmbedding() == null || slot.getRequirementsEmbedding().isEmpty())
                        && slot.getRequirements() != null
                        && !slot.getRequirements().trim().isEmpty())
                .collect(Collectors.toList());

        if (slotsWithoutEmbedding.isEmpty()) {
            log.info("임베딩 생성이 필요한 전시 슬롯이 없습니다.");
            return 0;
        }

        log.info("임베딩이 없는 전시 슬롯 {}개를 찾았습니다.", slotsWithoutEmbedding.size());

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

            log.info("전시 슬롯 {}개에 대한 임베딩을 성공적으로 생성하고 저장했습니다.", slotsWithoutEmbedding.size());
            return slotsWithoutEmbedding.size();

        } catch (Exception e) {
            log.error("전시 슬롯 배치 임베딩 생성 중 오류가 발생했습니다.", e);
            throw new RuntimeException("전시 슬롯 배치 임베딩 생성에 실패했습니다.", e);
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
