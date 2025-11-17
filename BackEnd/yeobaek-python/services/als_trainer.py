import pandas as pd
import numpy as np
from scipy.sparse import csr_matrix
import implicit
import pickle
from typing import List, Dict, Tuple
from schemas.models import RatingData
import os


class ALSTrainer:
    """ALS 모델 학습을 담당하는 클래스"""

    def __init__(self, model_path: str = "models/als_model.pkl"):
        self.model_path = model_path
        self.ALPHA_VALUE = 40  # ALS 신뢰도 가중치

    def train(self, ratings: List[RatingData]) -> Dict[str, int]:
        """
        평점 데이터를 받아서 ALS 모델을 학습하고 저장

        Args:
            ratings: 평점 데이터 리스트

        Returns:
            학습 결과 통계 (사용자 수, 작품 수)
        """
        # 1. RatingData 리스트를 DataFrame으로 변환
        data_df = pd.DataFrame([
            {
                'user_id': r.userId,
                'item_id': r.itemId,
                'rating': r.score
            }
            for r in ratings
        ])

        # 2. User ID와 Item ID를 Categorical 타입으로 변환하고 인덱스 매핑
        data_df['user_index'] = data_df['user_id'].astype("category").cat.codes
        data_df['item_index'] = data_df['item_id'].astype("category").cat.codes

        # 3. 원본 ID와 인덱스 간의 매핑 딕셔너리 생성
        user_map = dict(enumerate(data_df['user_id'].astype("category").cat.categories))
        item_map = dict(enumerate(data_df['item_id'].astype("category").cat.categories))

        reverse_user_map = {v: k for k, v in user_map.items()}
        reverse_item_map = {v: k for k, v in item_map.items()}

        # 4. ALS용 희소 행렬 (CSR Matrix) 생성
        user_item_matrix = csr_matrix(
            (data_df['rating'].astype(np.float64),
             (data_df['user_index'].values, data_df['item_index'].values)),
            shape=(len(user_map), len(item_map))
        )

        # 5. 신뢰도 행렬 생성
        confidence_matrix = (user_item_matrix * self.ALPHA_VALUE).astype(np.float64)
        confidence_matrix.eliminate_zeros()

        # 6. ALS 모델 생성 및 학습
        model = implicit.als.AlternatingLeastSquares(
            factors=50,
            regularization=0.01,
            iterations=20,
            random_state=42
        )

        # 전치 사용: (item × user) 형태로 학습
        # CSR 형식으로 명시적 변환하여 경고 방지
        item_user_matrix = confidence_matrix.T.tocsr()
        model.fit(item_user_matrix)

        # 7. 모델 및 매핑 정보 저장
        model_data = {
            'model': model,
            'user_map': user_map,
            'item_map': item_map,
            'reverse_user_map': reverse_user_map,
            'reverse_item_map': reverse_item_map
        }

        # models 디렉토리가 없으면 생성
        os.makedirs(os.path.dirname(self.model_path), exist_ok=True)

        with open(self.model_path, 'wb') as file:
            pickle.dump(model_data, file)

        return {
            'userCount': len(user_map),
            'itemCount': len(item_map)
        }
