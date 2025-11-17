import pickle
import numpy as np
from typing import List, Optional
import os


class ALSRecommender:
    """ALS 모델을 사용한 추천 서비스 (싱글톤 패턴)"""

    _instance = None
    _model_data = None

    def __new__(cls, model_path: str = "models/als_model.pkl"):
        if cls._instance is None:
            cls._instance = super().__new__(cls)
            cls._instance.model_path = model_path
            cls._instance._load_model()
        return cls._instance

    def _load_model(self):
        """저장된 모델 파일을 로드"""
        if not os.path.exists(self.model_path):
            raise FileNotFoundError(
                f"모델 파일을 찾을 수 없습니다: {self.model_path}\n"
                "먼저 /train API를 호출하여 모델을 학습해주세요."
            )

        with open(self.model_path, 'rb') as file:
            self._model_data = pickle.load(file)

    def reload_model(self):
        """모델 재로드 (학습 후 호출)"""
        self._load_model()

    def get_recommendations(self, user_id: int, top_n: int = 10) -> List[int]:
        """
        특정 사용자에게 추천 아이템 목록을 반환

        Args:
            user_id: 추천 대상 사용자 ID
            top_n: 추천할 아이템 개수

        Returns:
            추천 아이템 ID 리스트

        Raises:
            ValueError: 사용자 ID를 찾을 수 없는 경우
        """
        if self._model_data is None:
            raise RuntimeError("모델이 로드되지 않았습니다.")

        model = self._model_data['model']
        reverse_user_map = self._model_data['reverse_user_map']
        item_map = self._model_data['item_map']

        # 사용자 ID 확인
        if user_id not in reverse_user_map:
            raise ValueError(
                f"사용자 ID {user_id}를 찾을 수 없습니다. "
                f"사용 가능한 User ID: {sorted(reverse_user_map.keys())}"
            )

        # 사용자 인덱스 가져오기
        user_index = reverse_user_map[user_id]

        # ALS 임베딩을 사용한 추천 점수 계산
        # model.item_factors = 실제로는 user 임베딩 (fit에서 전치를 사용했기 때문)
        # model.user_factors = 실제로는 item 임베딩
        user_embedding = model.item_factors[user_index]  # (50,) - 사용자 임베딩
        item_embeddings = model.user_factors  # (n_items, 50) - 아이템 임베딩

        # 모든 아이템에 대한 예측 점수 계산
        all_scores = item_embeddings.dot(user_embedding)  # (n_items,) 벡터

        # 점수 기준 내림차순 정렬
        sorted_indices = np.argsort(all_scores)[::-1]

        # 상위 N개 아이템 ID 추출
        top_items = []
        for item_index in sorted_indices[:top_n]:
            item_id = item_map[item_index]
            top_items.append(int(item_id))

        return top_items
