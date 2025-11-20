"""
Sentence Transformers 기반 텍스트 임베딩 서비스
한국어 의미론적 유사도 계산을 위한 768차원 벡터 생성
"""

import os
from typing import List, Union
from dotenv import load_dotenv
from sentence_transformers import SentenceTransformer

# 환경 변수 로드
load_dotenv()


class SentenceEmbedder:
    """
    Sentence Transformers 모델을 사용한 텍스트 임베딩 서비스
    """

    _instance = None
    _model = None
    _model_name = None
    _cache_dir = None

    def __new__(cls):
        """싱글톤 패턴 구현"""
        if cls._instance is None:
            cls._instance = super().__new__(cls)
        return cls._instance

    def __init__(self):
        """
        모델 초기화
        환경 변수에서 모델 이름과 캐시 디렉토리 로드
        """
        if self._model is None:
            self._model_name = os.getenv("MODEL_NAME")
            self._cache_dir = os.getenv("MODEL_CACHE_DIR")
            self._load_model()

    def _load_model(self):
        """
        Sentence Transformers 모델 로드
        첫 실행 시 약 400MB 모델 다운로드
        """
        print(f"Loading Sentence Transformer model: {self._model_name}")
        print(f"Cache directory: {self._cache_dir}")

        self._model = SentenceTransformer(
            self._model_name,
            cache_folder=self._cache_dir
        )

        print(f"✅ Model loaded successfully! Embedding dimension: {self._model.get_sentence_embedding_dimension()}")

    def encode_single(self, text: str) -> List[float]:
        """
        단일 텍스트를 768차원 임베딩 벡터로 변환

        Args:
            text: 입력 텍스트 (작품 설명, 카페 설명 등)

        Returns:
            768차원 임베딩 벡터 (List[float])

        Example:
            >>> embedder = SentenceEmbedder()
            >>> embedding = embedder.encode_single("따뜻한 색감의 제주 바다 풍경")
            >>> len(embedding)
            768
        """
        if not text or not text.strip():
            raise ValueError("Text cannot be empty")

        # Sentence Transformers는 numpy array를 반환하므로 리스트로 변환
        embedding = self._model.encode(text, convert_to_numpy=True)
        return embedding.tolist()

    def encode_batch(self, texts: List[str], batch_size: int = 32) -> List[List[float]]:
        """
        여러 텍스트를 배치로 임베딩 벡터로 변환
        배치 처리로 속도 향상

        Args:
            texts: 입력 텍스트 리스트
            batch_size: 배치 크기 (기본값: 32)

        Returns:
            임베딩 벡터 리스트 (각 벡터는 768차원)

        Example:
            >>> embedder = SentenceEmbedder()
            >>> texts = ["작품 설명 1", "작품 설명 2", "작품 설명 3"]
            >>> embeddings = embedder.encode_batch(texts)
            >>> len(embeddings)
            3
            >>> len(embeddings[0])
            768
        """
        if not texts:
            raise ValueError("Texts list cannot be empty")

        # 빈 문자열 체크
        for i, text in enumerate(texts):
            if not text or not text.strip():
                raise ValueError(f"Text at index {i} is empty")

        # 배치 인코딩
        embeddings = self._model.encode(
            texts,
            batch_size=batch_size,
            convert_to_numpy=True,
            show_progress_bar=len(texts) > 100  # 100개 이상일 때만 진행률 표시
        )

        # numpy array를 리스트로 변환
        return [embedding.tolist() for embedding in embeddings]

    def get_model_info(self) -> dict:
        """
        모델 정보 반환

        Returns:
            모델 이름, 임베딩 차원, 캐시 디렉토리 정보
        """
        return {
            "model_name": self._model_name,
            "embedding_dimension": self._model.get_sentence_embedding_dimension(),
            "cache_dir": self._cache_dir
        }


# 전역 인스턴스 (싱글톤)
_embedder_instance = None


def get_embedder() -> SentenceEmbedder:
    """
    SentenceEmbedder 싱글톤 인스턴스 가져오기

    Returns:
        SentenceEmbedder 인스턴스
    """
    global _embedder_instance
    if _embedder_instance is None:
        _embedder_instance = SentenceEmbedder()
    return _embedder_instance
