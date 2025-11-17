from fastapi import FastAPI, HTTPException
from schemas.models import (
    TrainRequest,
    TrainResponse,
    RecommendRequest,
    RecommendationResponse
)
from services.als_trainer import ALSTrainer
from services.als_recommender import ALSRecommender

# FastAPI 애플리케이션 인스턴스 생성
app = FastAPI(
    title="Yeobaek Recommender API",
    description="ALS 기반 추천 시스템 API",
    version="1.0.0"
)

# 서비스 인스턴스
trainer = ALSTrainer()
recommender = None  # 모델 학습 후 초기화


# ============================================
# API 엔드포인트
# ============================================

@app.get("/health")
async def health_check():
    """헬스 체크 엔드포인트"""
    return {"status": "healthy"}


@app.post("/train", response_model=TrainResponse)
async def train_model(request: TrainRequest):
    """
    평점 데이터를 받아서 ALS 모델을 학습

    Args:
        request: 평점 데이터 배열을 포함한 요청 객체

    Returns:
        학습 결과 (상태, 메시지, 사용자 수, 작품 수)
    """
    try:
        # 모델 학습
        result = trainer.train(request.ratings)

        # 추천 서비스 초기화 또는 재로드
        global recommender
        if recommender is None:
            recommender = ALSRecommender()
        else:
            recommender.reload_model()

        return TrainResponse(
            status="success",
            message="Model trained successfully",
            userCount=result['userCount'],
            itemCount=result['itemCount']
        )
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"학습 중 오류 발생: {str(e)}")


@app.post("/recommend", response_model=RecommendationResponse)
async def get_recommendations(request: RecommendRequest):
    """
    특정 사용자에게 추천 아이템 목록을 반환

    Args:
        request: 사용자 ID와 추천 개수를 포함한 요청 객체

    Returns:
        추천 아이템 ID 목록
    """
    global recommender

    if recommender is None:
        raise HTTPException(
            status_code=400,
            detail="모델이 학습되지 않았습니다. 먼저 /train API를 호출해주세요."
        )

    try:
        items = recommender.get_recommendations(request.userId, request.topN)
        return RecommendationResponse(items=items)
    except ValueError as e:
        raise HTTPException(status_code=404, detail=str(e))
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"추천 중 오류 발생: {str(e)}")


# ============================================
# 서버 실행 (개발용)
# ============================================

if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=8000)
