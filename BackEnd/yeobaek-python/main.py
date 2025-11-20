from fastapi import FastAPI, HTTPException
from schemas.models import (
    TrainRequest,
    TrainResponse,
    EmbeddingRequest,
    EmbeddingResponse,
    BatchEmbeddingRequest,
    BatchEmbeddingResponse,
    ArtworkRecommendForSlotRequest,
    CafeRecommendRequest,
    ArtworkItem,
    CafeItem,
    ArtworkHybridResponse,
    CafeHybridResponse
)
from services.cf_trainer import CFTrainer
from services.sentence_embedder import get_embedder
from services.hybrid_recommender import HybridRecommender

# FastAPI 애플리케이션 인스턴스 생성
app = FastAPI(
    title="Yeobaek Recommender API",
    description="ALS 기반 협업 필터링 + CBF 하이브리드 추천 시스템 API",
    version="1.0.0"
)

# 서비스 인스턴스
trainer = CFTrainer()
embedder = None  # 첫 요청 시 초기화
hybrid_recommender = None  # 첫 요청 시 초기화


# ============================================
# API 엔드포인트
# ============================================

@app.get("/health")
async def health_check():
    """헬스 체크 엔드포인트"""
    return {"status": "healthy"}


# ============================================
# CF 모델 학습 엔드포인트
# ============================================

@app.post("/train/artworks", response_model=TrainResponse)
async def train_artwork_model(request: TrainRequest):
    """
    '사용자-작품' 평점 데이터를 기반으로 CF 모델을 학습합니다.
    모델은 'models/als_artwork_model.pkl'에 저장됩니다.
    """
    try:
        model_path = "models/als_artwork_model.pkl"
        result = trainer.train(request.ratings, model_path)

        # 학습 후 하이브리드 추천기 인스턴스 리셋 (다음 요청 시 새로 로드)
        global hybrid_recommender
        hybrid_recommender = None

        return TrainResponse(
            status="success",
            message=f"Artwork model trained successfully and saved to {model_path}",
            userCount=result['userCount'],
            itemCount=result['itemCount']
        )
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Artwork model training failed: {str(e)}")


@app.post("/train/cafes", response_model=TrainResponse)
async def train_cafe_model(request: TrainRequest):
    """
    '사용자-카페' 평점 데이터를 기반으로 CF 모델을 학습합니다.
    모델은 'models/als_cafe_model.pkl'에 저장됩니다.
    """
    try:
        model_path = "models/als_cafe_model.pkl"
        result = trainer.train(request.ratings, model_path)

        # 학습 후 하이브리드 추천기 인스턴스 리셋 (다음 요청 시 새로 로드)
        global hybrid_recommender
        hybrid_recommender = None

        return TrainResponse(
            status="success",
            message=f"Cafe model trained successfully and saved to {model_path}",
            userCount=result['userCount'],
            itemCount=result['itemCount']
        )
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Cafe model training failed: {str(e)}")


# ============================================
# CBF 임베딩 엔드포인트
# ============================================

@app.post("/embeddings/generate", response_model=EmbeddingResponse)
async def generate_embedding(request: EmbeddingRequest):
    """
    단일 텍스트를 768차원 임베딩 벡터로 변환

    Args:
        request: 텍스트를 포함한 요청 객체

    Returns:
        768차원 임베딩 벡터
    """
    global embedder

    try:
        # 첫 요청 시 임베더 초기화 (지연 로딩)
        if embedder is None:
            embedder = get_embedder()

        embedding = embedder.encode_single(request.text)
        return EmbeddingResponse(embedding=embedding)

    except ValueError as e:
        raise HTTPException(status_code=400, detail=str(e))
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"임베딩 생성 중 오류 발생: {str(e)}")


@app.post("/embeddings/batch", response_model=BatchEmbeddingResponse)
async def generate_batch_embeddings(request: BatchEmbeddingRequest):
    """
    여러 텍스트를 배치로 임베딩 벡터로 변환

    Args:
        request: 텍스트 리스트를 포함한 요청 객체

    Returns:
        각 텍스트에 대한 768차원 임베딩 벡터 리스트
    """
    global embedder

    try:
        # 첫 요청 시 임베더 초기화 (지연 로딩)
        if embedder is None:
            embedder = get_embedder()

        embeddings = embedder.encode_batch(request.texts)
        return BatchEmbeddingResponse(embeddings=embeddings)

    except ValueError as e:
        raise HTTPException(status_code=400, detail=str(e))
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"배치 임베딩 생성 중 오류 발생: {str(e)}")


# ============================================
# 하이브리드 추천 엔드포인트 (CF + CBF)
# ============================================

@app.post("/recommend/artworks-for-slot", response_model=ArtworkHybridResponse)
async def recommend_artworks_for_slot(request: ArtworkRecommendForSlotRequest):
    """
    슬롯에 어울리는 작품 추천 (CF + CBF 하이브리드)

    Args:
        request: 슬롯 ID, 추천 개수를 포함한 요청 객체

    Returns:
        추천 작품 ID 및 점수 리스트
    """
    global hybrid_recommender

    try:
        # 첫 요청 시 하이브리드 추천 시스템 초기화
        if hybrid_recommender is None:
            hybrid_recommender = HybridRecommender()

        # 하이브리드 추천 실행
        recommendations = hybrid_recommender.recommend_artworks_for_slot(
            slot_id=request.slot_id,
            top_n=request.top_n
        )

        # 응답 형식 변환
        items = [
            ArtworkItem(artworkId=rec['artwork_id'], score=rec['score'])
            for rec in recommendations
        ]

        return ArtworkHybridResponse(recommendations=items)

    except ValueError as e:
        raise HTTPException(status_code=404, detail=str(e))
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"작품 추천 중 오류 발생: {str(e)}")


@app.post("/recommend/cafes-for-artwork", response_model=CafeHybridResponse)
async def recommend_cafes_for_artwork(request: CafeRecommendRequest):
    """
    작품에 어울리는 카페 추천 (CF + CBF 하이브리드)

    Args:
        request: 작품 ID, 추천 개수를 포함한 요청 객체

    Returns:
        추천 카페 ID 및 점수 리스트
    """
    global hybrid_recommender

    try:
        # 첫 요청 시 하이브리드 추천 시스템 초기화
        if hybrid_recommender is None:
            hybrid_recommender = HybridRecommender()

        # 하이브리드 추천 실행
        recommendations = hybrid_recommender.recommend_cafes_for_artwork(
            artwork_id=request.artwork_id,
            top_n=request.top_n
        )

        # 응답 형식 변환
        items = [
            CafeItem(cafeId=rec['cafe_id'], score=rec['score'])
            for rec in recommendations
        ]

        return CafeHybridResponse(recommendations=items)

    except ValueError as e:
        raise HTTPException(status_code=404, detail=str(e))
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"카페 추천 중 오류 발생: {str(e)}")


# ============================================
# 서버 실행 (개발용)
# ============================================

if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=8000)