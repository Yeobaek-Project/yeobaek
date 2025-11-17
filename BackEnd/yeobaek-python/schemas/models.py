from pydantic import BaseModel
from typing import List


class RatingData(BaseModel):
    """평점 데이터 모델"""
    userId: int
    itemId: int
    score: float


class TrainRequest(BaseModel):
    """학습 요청 모델"""
    ratings: List[RatingData]


class TrainResponse(BaseModel):
    """학습 응답 모델"""
    status: str
    message: str
    userCount: int
    itemCount: int


class RecommendRequest(BaseModel):
    """추천 요청 모델"""
    userId: int
    topN: int = 10


class RecommendationResponse(BaseModel):
    """추천 응답 모델"""
    items: List[int]
