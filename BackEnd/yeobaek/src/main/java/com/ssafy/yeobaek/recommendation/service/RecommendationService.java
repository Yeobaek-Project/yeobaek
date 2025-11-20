package com.ssafy.yeobaek.recommendation.service;

import com.ssafy.yeobaek.recommendation.dto.request.*;
import com.ssafy.yeobaek.recommendation.dto.response.*;

import java.util.List;

public interface RecommendationService {

    /**
     * 작품 추천 모델 학습
     *
     * @param ratings 암묵적 피드백 데이터 (userId, itemId, score)
     */
    void trainArtworkModel(List<RatingData> ratings);

    /**
     * 카페 추천 모델 학습
     *
     * @param ratings 암묵적 피드백 데이터 (userId, itemId, score)
     */
    void trainCafeModel(List<RatingData> ratings);

    /**
     * 단일 텍스트 임베딩 생성
     * @param text 임베딩할 텍스트
     * @return 768차원 임베딩 벡터
     */
    EmbeddingResponse generateEmbedding(String text);

    /**
     * 배치 텍스트 임베딩 생성
     * @param texts 임베딩할 텍스트 리스트
     * @return 각 텍스트에 대한 768차원 임베딩 벡터 리스트
     */
    BatchEmbeddingResponse generateEmbeddingsBatch(List<String> texts);

    /**
     * 슬롯에 어울리는 작품 추천 (CF + CBF 하이브리드)
     * @param slotId 슬롯 ID
     * @param topN 추천할 작품 개수
     * @return 추천 작품 ID 및 점수 리스트
     */
    ArtworkHybridResponse recommendArtworksForSlot(Long slotId, Integer topN);

    /**
     * 작품에 어울리는 카페 추천 (CF + CBF 하이브리드)
     * @param artworkId 작품 ID
     * @param topN 추천할 카페 개수
     * @return 추천 카페 ID 및 점수 리스트
     */
    CafeHybridResponse recommendCafesForArtwork(Long artworkId, Integer topN);
}
