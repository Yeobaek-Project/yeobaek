"""
Content-Based Filtering (CBF) 추천 로직
텍스트 유사도, 크기 호환성, 카테고리 매칭을 통한 추천 점수 계산
"""

import numpy as np
from typing import List, Dict, Any, Tuple
from sklearn.metrics.pairwise import cosine_similarity


# 카테고리 호환성 매핑 테이블
# 카페 카테고리(분위기/스타일) vs 작품 카테고리(장르)
CATEGORY_COMPATIBILITY = {
    # 카페 분위기 -> 작품 장르별 호환성 점수 (0~1)
    "모던": {
        "OTHER": 1.0,
        "PORTRAIT": 0.8,
        "STILL_LIFE": 0.7,
        "LANDSCAPE": 0.5,
        "GENRE": 0.4,
        "HISTORY": 0.3
    },
    "빈티지": {
        "HISTORY": 1.0,
        "PORTRAIT": 0.8,
        "GENRE": 0.7,
        "LANDSCAPE": 0.6,
        "STILL_LIFE": 0.5,
        "OTHER": 0.4
    },
    "미니멀": {
        "OTHER": 1.0,
        "STILL_LIFE": 0.7,
        "LANDSCAPE": 0.6,
        "PORTRAIT": 0.5,
        "GENRE": 0.3,
        "HISTORY": 0.2
    },
    "인더스트리얼": {
        "OTHER": 1.0,
        "GENRE": 0.7,
        "PORTRAIT": 0.6,
        "STILL_LIFE": 0.5,
        "LANDSCAPE": 0.4,
        "HISTORY": 0.3
    },
    "클래식": {
        "HISTORY": 1.0,
        "PORTRAIT": 0.9,
        "STILL_LIFE": 0.8,
        "LANDSCAPE": 0.7,
        "GENRE": 0.6,
        "OTHER": 0.4
    },
    "레트로": {
        "GENRE": 1.0,
        "PORTRAIT": 0.8,
        "STILL_LIFE": 0.7,
        "OTHER": 0.6,
        "LANDSCAPE": 0.5,
        "HISTORY": 0.4
    },
    "스칸디나비아": {
        "LANDSCAPE": 1.0,
        "STILL_LIFE": 0.8,
        "OTHER": 0.7,
        "PORTRAIT": 0.5,
        "GENRE": 0.4,
        "HISTORY": 0.2
    },
    "내추럴": {
        "LANDSCAPE": 1.0,    # 풍경화
        "STILL_LIFE": 0.8,   # 정물화
        "GENRE": 0.6,
        "PORTRAIT": 0.4,
        "HISTORY": 0.2,
        "OTHER": 0.5
    }
}


class CBFRecommender:
    """Content-Based Filtering 추천 시스템"""

    def __init__(self):
        """CBF 추천 시스템 초기화"""
        self.text_weight = 0.4      # 텍스트 유사도 가중치 (40%)
        self.size_weight = 0.4      # 크기 호환성 가중치 (40%)
        self.category_weight = 0.2  # 카테고리 매칭 가중치 (20%)

    def calculate_text_similarity(
        self,
        target_embedding: List[float],
        candidate_embeddings: List[List[float]]
    ) -> List[float]:
        """
        텍스트 코사인 유사도 계산

        Args:
            target_embedding: 기준 임베딩 (카페 또는 작품) - 768차원
            candidate_embeddings: 비교할 임베딩 리스트 - 각 768차원

        Returns:
            유사도 점수 리스트 (0~1, 1에 가까울수록 유사)
        """
        if not target_embedding or not candidate_embeddings:
            return []

        # numpy array로 변환
        target = np.array(target_embedding).reshape(1, -1)
        candidates = np.array(candidate_embeddings)

        # 코사인 유사도 계산 (결과: -1 ~ 1)
        similarities = cosine_similarity(target, candidates)[0]

        # 0~1 범위로 정규화: (sim + 1) / 2
        # -1 -> 0, 0 -> 0.5, 1 -> 1
        normalized = (similarities + 1) / 2

        return normalized.tolist()

    def calculate_size_compatibility(
        self,
        artwork: Dict[str, Any],
        display_walls: List[Dict[str, Any]]
    ) -> float:
        """
        작품과 전시벽 간 크기 호환성 계산

        Args:
            artwork: 작품 정보 (width_cm, height_cm 포함)
            display_walls: 전시벽 정보 리스트 (width_cm, height_cm 포함)

        Returns:
            크기 호환성 점수 (0~1)
            - 0: 걸 수 없음
            - 1: 이상적인 크기 (전시벽 면적의 60~80%)
        """
        if not display_walls:
            return 0.0

        artwork_width = artwork.get('width_cm', 0)
        artwork_height = artwork.get('height_cm', 0)

        if artwork_width <= 0 or artwork_height <= 0:
            return 0.0

        artwork_area = artwork_width * artwork_height

        # 모든 전시벽 중 최고 점수 선택
        max_score = 0.0

        for wall in display_walls:
            wall_width = wall.get('width_cm', 0)
            wall_height = wall.get('height_cm', 0)

            if wall_width <= 0 or wall_height <= 0:
                continue

            # 1. 물리적으로 걸릴 수 있는지 확인
            if artwork_width > wall_width or artwork_height > wall_height:
                continue  # 걸 수 없음 -> 0점

            # 2. 면적 비율 계산
            wall_area = wall_width * wall_height
            area_ratio = artwork_area / wall_area

            # 3. 이상적인 비율: 60~80%
            if 0.6 <= area_ratio <= 0.8:
                score = 1.0
            elif area_ratio < 0.6:
                # 너무 작음: 0.4에서 0.6까지 선형적으로 0.0에서 1.0으로 증가
                score = max(0, (area_ratio - 0.4) / 0.2)
            else:
                # 너무 큼: 0.8에서 1.0까지 선형적으로 1.0에서 0.0으로 감소
                score = max(0, (1.0 - area_ratio) / 0.2)

            max_score = max(max_score, score)

        return max_score

    def calculate_category_compatibility(
        self,
        cafe_categories: List[str],
        artwork_category: str
    ) -> float:
        """
        카페 분위기/스타일과 작품 장르 간 호환성 계산

        Args:
            cafe_categories: 카페 카테고리 리스트 (예: ["자연친화적", "아늑한"])
            artwork_category: 작품 카테고리 (예: "LANDSCAPE")

        Returns:
            카테고리 호환성 점수 (0~1)
        """
        if not cafe_categories or not artwork_category:
            return 0.5  # 기본값

        scores = []

        for cafe_cat in cafe_categories:
            # 호환성 테이블에서 점수 조회
            compatibility = CATEGORY_COMPATIBILITY.get(cafe_cat, {})
            score = compatibility.get(artwork_category, 0.5)  # 없으면 기본값 0.5
            scores.append(score)

        # 평균 점수 반환
        return sum(scores) / len(scores) if scores else 0.5

    def calculate_cbf_score(
        self,
        target_embedding: List[float],
        candidate_embedding: List[float],
        artwork: Dict[str, Any],
        display_walls: List[Dict[str, Any]],
        cafe_categories: List[str]
    ) -> float:
        """
        단일 후보에 대한 CBF 종합 점수 계산

        Args:
            target_embedding: 기준 임베딩 (카페)
            candidate_embedding: 후보 임베딩 (작품)
            artwork: 작품 정보
            display_walls: 전시벽 정보
            cafe_categories: 카페 카테고리

        Returns:
            CBF 종합 점수 (0~1)
        """
        # 1. 텍스트 유사도 (40%)
        text_sim = self.calculate_text_similarity(
            target_embedding,
            [candidate_embedding]
        )[0]

        # 2. 크기 호환성 (40%)
        size_compat = self.calculate_size_compatibility(artwork, display_walls)

        # 3. 카테고리 매칭 (20%)
        category_compat = self.calculate_category_compatibility(
            cafe_categories,
            artwork.get('art_category', 'OTHER')
        )

        # 가중 합산
        cbf_score = (
            text_sim * self.text_weight +
            size_compat * self.size_weight +
            category_compat * self.category_weight
        )

        return cbf_score

    def calculate_artwork_cbf_scores(
        self,
        cafe_embedding: List[float],
        artworks: List[Dict[str, Any]],
        display_walls: List[Dict[str, Any]],
        cafe_categories: List[str],
        top_n: int = 10
    ) -> List[Tuple[int, float]]:
        """
        카페에 어울리는 작품 추천

        Args:
            cafe_embedding: 카페 설명 임베딩
            artworks: 작품 리스트 (각각 embedding 포함)
            display_walls: 카페의 전시벽 리스트
            cafe_categories: 카페 카테고리 리스트
            top_n: 추천할 작품 개수

        Returns:
            (작품 ID, CBF 점수) 튜플 리스트 (점수 높은 순)
        """
        recommendations = []

        for artwork in artworks:
            artwork_embedding = artwork.get('embedding')

            if not artwork_embedding:
                continue  # 임베딩 없으면 스킵

            # CBF 점수 계산
            score = self.calculate_cbf_score(
                cafe_embedding,
                artwork_embedding,
                artwork,
                display_walls,
                cafe_categories
            )

            recommendations.append((artwork['id'], score))

        # 점수 높은 순으로 정렬
        recommendations.sort(key=lambda x: x[1], reverse=True)

        # 상위 N개 반환
        return recommendations[:top_n]

    def calculate_cafe_cbf_scores(
        self,
        artwork_embedding: List[float],
        artwork: Dict[str, Any],
        all_slots: List[Dict[str, Any]],
        cafe_categories_map: Dict[int, List[str]]
    ) -> List[Tuple[int, float]]:
        """
        작품에 어울리는 카페의 CBF 점수 계산 (슬롯 기반)

        Args:
            artwork_embedding: 작품 설명 임베딩
            artwork: 작품 정보 (width_cm, height_cm, art_category 포함)
            all_slots: 모든 슬롯 정보 리스트 (각각 embedding, width_cm, height_cm, cafe_id 포함)
            cafe_categories_map: {cafe_id: [category_names]} 딕셔너리

        Returns:
            (카페 ID, CBF 점수) 튜플 리스트 (점수 높은 순)
        """
        cafe_cbf_scores = {}

        if not artwork_embedding or not all_slots:
            return []

        for slot in all_slots:
            slot_embedding = slot.get('embedding')
            if not slot_embedding:
                continue

            # CBF 점수 계산 (작품 ↔ 슬롯)
            # calculate_cbf_score의 target_embedding은 기준이 되는 임베딩 (여기서는 슬롯)
            # candidate_embedding은 비교 대상 (여기서는 작품)
            score = self.calculate_cbf_score(
                target_embedding=slot_embedding,
                candidate_embedding=artwork_embedding,
                artwork=artwork,
                display_walls=[{"width_cm": slot.get("width_cm"), "height_cm": slot.get("height_cm")}],
                cafe_categories=cafe_categories_map.get(slot['cafe_id'], [])
            )

            # 해당 슬롯이 속한 카페의 최고 CBF 점수를 갱신
            cafe_id = slot['cafe_id']
            current_max_score = cafe_cbf_scores.get(cafe_id, 0.0)
            cafe_cbf_scores[cafe_id] = max(current_max_score, score)

        # 딕셔너리를 (카페 ID, 점수) 튜플 리스트로 변환
        recommendations = list(cafe_cbf_scores.items())
        recommendations.sort(key=lambda x: x[1], reverse=True)

        return recommendations
