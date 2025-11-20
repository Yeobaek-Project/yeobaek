import pickle
import numpy as np
from typing import List, Tuple
import os


class CFRecommender:
    """ALS 모델을 사용한 추천 서비스"""

    def __init__(self, model_path: str):
        """
        CFRecommender 초기화 및 모델 로드

        Args:
            model_path: 불러올 모델 파일의 경로
        """
        if not os.path.exists(model_path):
            raise FileNotFoundError(
                f"모델 파일을 찾을 수 없습니다: {model_path}\n"
                "먼저 학습 API를 호출하여 모델을 생성해주세요."
            )
        self.model_path = model_path
        self._model_data = None
        self._load_model()

    def _load_model(self):
        """저장된 모델 파일을 로드"""
        with open(self.model_path, 'rb') as file:
            self._model_data = pickle.load(file)



    def get_recommendations(self, user_id: int, top_n: int = 10) -> List[Tuple[int, float]]:
        """
        특정 사용자에게 추천 아이템 목록과 점수를 반환

        Args:
            user_id: 추천 대상 사용자 ID
            top_n: 추천할 아이템 개수

        Returns:
            (아이템 ID, 정규화된 점수) 튜플 리스트

        Raises:
            ValueError: 사용자 ID를 찾을 수 없는 경우
        """
        if self._model_data is None:
            raise RuntimeError("모델이 로드되지 않았습니다.")

        model = self._model_data['model']
        reverse_user_map = self._model_data['reverse_user_map']
        item_map = self._model_data['item_map']

        if user_id not in reverse_user_map:
            raise ValueError(f"사용자 ID {user_id}를 찾을 수 없습니다.")

        user_index = reverse_user_map[user_id]
        user_embedding = model.item_factors[user_index]
        item_embeddings = model.user_factors
        all_scores = item_embeddings.dot(user_embedding)

        # 이미 본 아이템 제외 (선택적, 현재는 제외하지 않음)

        # 상위 N개 아이템 ID와 점수 추출
        sorted_indices = np.argsort(all_scores)[::-1][:top_n]
        
        top_items = []
        for item_index in sorted_indices:
            item_id = int(item_map[item_index])
            score = all_scores[item_index]
            top_items.append((item_id, score))

        # 점수 정규화 (0~1 범위)
        if not top_items:
            return []

        scores = np.array([item[1] for item in top_items])
        if scores.min() == scores.max():
            normalized_scores = [0.5] * len(scores)
        else:
            normalized_scores = (scores - scores.min()) / (scores.max() - scores.min())

        # 정규화된 점수와 함께 반환
        recommendations = [
            (top_items[i][0], normalized_scores[i])
            for i in range(len(top_items))
        ]

        return recommendations
