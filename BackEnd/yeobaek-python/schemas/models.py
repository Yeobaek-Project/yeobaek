from pydantic import BaseModel
from typing import List


# ===== CF ALS 관련 스키마 =====

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


# ===== CBF 임베딩 관련 스키마 =====

class EmbeddingRequest(BaseModel):
    """단일 텍스트 임베딩 요청"""
    text: str


class EmbeddingResponse(BaseModel):
    """단일 텍스트 임베딩 응답"""
    embedding: List[float]


class BatchEmbeddingRequest(BaseModel):
    """배치 텍스트 임베딩 요청"""
    texts: List[str]


class BatchEmbeddingResponse(BaseModel):
    """배치 텍스트 임베딩 응답"""
    embeddings: List[List[float]]


# ===== 하이브리드 추천 관련 스키마 =====

class ArtworkRecommendForSlotRequest(BaseModel):
    """슬롯에 어울리는 작품 추천 요청"""
    slot_id: int
    top_n: int = 10


class CafeRecommendRequest(BaseModel):
    """작품에 어울리는 카페 추천 요청"""
    artwork_id: int
    top_n: int = 10


class ArtworkItem(BaseModel):
    """추천 작품 아이템"""
    artworkId: int
    score: float


class CafeItem(BaseModel):
    """추천 카페 아이템"""
    cafeId: int
    score: float


class ArtworkHybridResponse(BaseModel):
    """작품 추천 하이브리드 응답"""
    recommendations: List[ArtworkItem]


class CafeHybridResponse(BaseModel):
    """카페 추천 하이브리드 응답"""
    recommendations: List[CafeItem]