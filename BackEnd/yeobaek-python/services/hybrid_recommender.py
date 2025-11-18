"""
하이브리드 추천 시스템
CF (ALS 협업 필터링) + CBF (콘텐츠 기반 필터링) 결합
"""

import numpy as np
from typing import List, Dict, Any, Tuple

from services.cf_recommender import CFRecommender
from services.cbf_recommender import CBFRecommender
import database


class HybridRecommender:
    """
    CF(Collaborative Filtering) + CBF(Content-Based Filtering) 하이브리드 추천
    사용자 행동 데이터 기반으로 동적 가중치 조정
    """

    def __init__(
        self,
        artwork_model_path: str = "models/als_artwork_model.pkl",
        cafe_model_path: str = "models/als_cafe_model.pkl"
    ):
        """
        하이브리드 추천 시스템 초기화
        - 작품 추천 CF 모델과 카페 추천 CF 모델을 별도로 로드
        """
        try:
            self.artwork_cf_recommender = CFRecommender(artwork_model_path)
        except FileNotFoundError:
            self.artwork_cf_recommender = None
            print(f"Warning: Artwork CF model not found at {artwork_model_path}. CF for artworks will be disabled.")

        try:
            self.cafe_cf_recommender = CFRecommender(cafe_model_path)
        except FileNotFoundError:
            self.cafe_cf_recommender = None
            print(f"Warning: Cafe CF model not found at {cafe_model_path}. CF for cafes will be disabled.")

        self.cbf_recommender = CBFRecommender()


    def _get_hybrid_weights(self, user_behavior_count: int) -> Tuple[float, float]:
        """
        사용자 행동 데이터 개수에 따라 CF/CBF 가중치 결정
        """
        if user_behavior_count < 5:
            return 0.3, 0.7  # Cold-start
        elif user_behavior_count <= 20:
            return 0.5, 0.5  # 균형
        else:
            return 0.7, 0.3  # CF 우선

    def _normalize_scores(self, scores: List[float]) -> List[float]:
        """
        점수를 Min-Max 정규화 (0~1 범위)
        """
        if not scores:
            return []
        scores_array = np.array(scores)
        if scores_array.min() == scores_array.max():
            return [0.5] * len(scores)
        normalized = (scores_array - scores_array.min()) / (scores_array.max() - scores_array.min())
        return normalized.tolist()

    def recommend_artworks_for_slot(
        self,
        slot_id: int,
        top_n: int = 10
    ) -> List[Dict[str, Any]]:
        """
        슬롯에 어울리는 작품 추천 (CF + CBF 하이브리드)
        """
        # 1. 데이터 조회
        slot_details = database.get_slot_details_for_recommendation(slot_id)
        if not slot_details:
            raise ValueError(f"Slot with id {slot_id} not found")

        artworks = database.get_all_available_artworks()
        if not artworks:
            return []

        # 슬롯에 연결된 카페의 owner_id를 CF의 user_id로 사용
        owner_id = slot_details.get('owner_id')
        if owner_id is None:
            raise ValueError(f"Owner ID not found for slot {slot_id}")

        user_behavior_count = database.get_user_behavior_count(owner_id)

        # 2. CF 점수 계산 (ALS)
        cf_scores_dict = {}
        if self.artwork_cf_recommender:
            try:
                # get_recommendations는 이제 (id, score) 튜플 리스트를 반환
                cf_recommendations = self.artwork_cf_recommender.get_recommendations(owner_id, top_n=len(artworks))
                cf_scores_dict = dict(cf_recommendations)
            except Exception as e:
                print(f"Artwork CF recommendation failed for owner {owner_id}: {e}")

        # 3. CBF 점수 계산
        slot_embedding = slot_details.get('embedding')
        cbf_scores_dict = {}
        if slot_embedding:
            wall_info = {
                "width_cm": slot_details.get("width_cm"),
                "height_cm": slot_details.get("height_cm")
            }
            cafe_categories = slot_details.get("categories", [])
            cbf_recommendations = self.cbf_recommender.calculate_artwork_cbf_scores(
                slot_embedding,
                artworks,
                [wall_info],
                cafe_categories,
                top_n=len(artworks)
            )
            cbf_scores_dict = {artwork_id: score for artwork_id, score in cbf_recommendations}

        # 4. 하이브리드 점수 계산
        cf_weight, cbf_weight = self._get_hybrid_weights(user_behavior_count)
        hybrid_scores = []

        all_artwork_ids_in_cbf = set(cbf_scores_dict.keys())
        all_artwork_ids_in_cf = set(cf_scores_dict.keys())
        all_relevant_artwork_ids = all_artwork_ids_in_cbf.union(all_artwork_ids_in_cf)

        for artwork_id in all_relevant_artwork_ids:
            cf_score = cf_scores_dict.get(artwork_id, 0.0)
            cbf_score = cbf_scores_dict.get(artwork_id, 0.0)
            hybrid_score = cf_score * cf_weight + cbf_score * cbf_weight
            hybrid_scores.append({"artwork_id": artwork_id, "score": hybrid_score})

        # 5. 점수 정규화 및 반환
        scores_only = [item['score'] for item in hybrid_scores]
        normalized_scores = self._normalize_scores(scores_only)
        for i, item in enumerate(hybrid_scores):
            item['score'] = normalized_scores[i]

        hybrid_scores.sort(key=lambda x: x['score'], reverse=True)
        return [
            {"artwork_id": item['artwork_id'], "score": round(item['score'], 4)}
            for item in hybrid_scores[:top_n]
        ]

    def recommend_cafes_for_artwork(
        self,
        artwork_id: int,
        top_n: int = 10
    ) -> List[Dict[str, Any]]:
        """
        작품에 어울리는 카페 추천 (CF + CBF 하이브리드)
        - CBF: 작품과 모든 카페의 모든 슬롯을 비교하여, 카페별 가장 높은 점수를 해당 카페의 CBF 점수로 책정
        - CF: 작품의 작가(artist_id)를 기준으로 카페 추천 점수 계산
        """
        # 1. 데이터 조회
        artwork = database.get_artwork_with_embedding(artwork_id)
        if not artwork:
            raise ValueError(f"Artwork with id {artwork_id} not found")

        all_slots = database.get_all_slots_for_recommendation()
        if not all_slots:
            return []

        artist_id = artwork.get('artist_id')
        if artist_id is None:
            raise ValueError(f"Artist ID not found for artwork {artwork_id}")

        # 2. CF 점수 계산 (ALS - 사용자-카페 모델 사용)
        cf_scores_dict = {}
        if self.cafe_cf_recommender:
            try:
                # 작가 ID를 사용하여 카페 추천 (id, score) 튜플 리스트를 받음
                cf_recommendations = self.cafe_cf_recommender.get_recommendations(artist_id, top_n=len(all_slots))
                cf_scores_dict = dict(cf_recommendations)
            except Exception as e:
                print(f"Cafe CF recommendation for artist {artist_id} failed: {e}")

        # 3. CBF 점수 계산 (작품 vs 모든 슬롯)
        artwork_embedding = artwork.get('embedding')
        cafe_cbf_scores = {}

        if artwork_embedding:
            # 모든 카페의 카테고리를 미리 조회하여 딕셔너리에 저장 (DB 조회 최소화)
            all_cafe_ids = {slot['cafe_id'] for slot in all_slots}
            cafe_categories_map = {
                cafe_id: database.get_cafe_categories(cafe_id) for cafe_id in all_cafe_ids
            }

            for slot in all_slots:
                slot_embedding = slot.get('embedding')
                if not slot_embedding:
                    continue

                # CBF 점수 계산
                cbf_score = self.cbf_recommender.calculate_cbf_score(
                    target_embedding=slot_embedding,  # 기준을 슬롯 임베딩으로 변경
                    candidate_embedding=artwork_embedding,
                    artwork=artwork,
                    display_walls=[{"width_cm": slot.get("width_cm"), "height_cm": slot.get("height_cm")}],
                    cafe_categories=cafe_categories_map.get(slot['cafe_id'], [])
                )

                # 해당 슬롯이 속한 카페의 최고 CBF 점수를 갱신
                cafe_id = slot['cafe_id']
                current_max_score = cafe_cbf_scores.get(cafe_id, 0.0)
                cafe_cbf_scores[cafe_id] = max(current_max_score, cbf_score)

        # 4. 하이브리드 점수 계산
        # 작가의 행동 데이터 개수를 기반으로 가중치 결정
        user_behavior_count = database.get_user_behavior_count(artist_id)
        cf_weight, cbf_weight = self._get_hybrid_weights(user_behavior_count)

        hybrid_scores = []
        all_cafe_ids_in_cbf = set(cafe_cbf_scores.keys())
        all_cafe_ids_in_cf = set(cf_scores_dict.keys())
        all_relevant_cafe_ids = all_cafe_ids_in_cbf.union(all_cafe_ids_in_cf)

        for cafe_id in all_relevant_cafe_ids:
            cf_score = cf_scores_dict.get(cafe_id, 0.0)
            cbf_score = cafe_cbf_scores.get(cafe_id, 0.0)
            hybrid_score = cf_score * cf_weight + cbf_score * cbf_weight
            hybrid_scores.append({"cafe_id": cafe_id, "score": hybrid_score})

        # 5. 점수 정규화 및 반환
        scores_only = [item['score'] for item in hybrid_scores]
        normalized_scores = self._normalize_scores(scores_only)
        for i, item in enumerate(hybrid_scores):
            item['score'] = normalized_scores[i]

        hybrid_scores.sort(key=lambda x: x['score'], reverse=True)
        return [
            {"cafe_id": item['cafe_id'], "score": round(item['score'], 4)}
            for item in hybrid_scores[:top_n]
        ]
