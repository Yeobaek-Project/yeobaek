# Yeobaek Recommender API

ALS(Alternating Least Squares) 기반 작품 추천 시스템 API

## 📋 목차

- [환경 요구사항](#환경-요구사항)
- [설치 방법](#설치-방법)
- [서버 실행](#서버-실행)
- [API 사용법](#api-사용법)
- [프로젝트 구조](#프로젝트-구조)

---

## 🔧 환경 요구사항

- **Python**: 3.12 이상
- **Conda** 또는 **Miniforge/Anaconda**: 필수

---

## 📦 설치 방법

### 1. Conda 환경 생성

```bash
# conda 환경 생성 (Python 3.12)
conda create -n yeobaek python=3.12 -y

# 환경 활성화
conda activate yeobaek
```

### 2. 필수 라이브러리 설치

#### 방법 A: Conda + pip 조합 (권장)

```bash
# conda로 C++ 컴파일이 필요한 패키지 설치
conda install -c conda-forge implicit pandas numpy scipy -y

# pip로 나머지 패키지 설치
pip install fastapi uvicorn[standard] pydantic
```

#### 방법 B: requirements.txt 사용

```bash
# conda로 implicit 설치
conda install -c conda-forge implicit -y

# pip로 나머지 설치
pip install -r requirements.txt
```

### 3. 설치 확인

```bash
# Python 버전 및 경로 확인
python --version
python -c "import sys; print(sys.executable)"

# 주요 패키지 확인
python -c "import implicit; print('implicit:', implicit.__version__)"
python -c "import fastapi; print('fastapi:', fastapi.__version__)"
```

---

## 🚀 서버 실행

### 개발 모드 (Hot Reload)

```bash
# conda 환경 활성화 (중요!)
conda activate yeobaek

# 프로젝트 디렉토리로 이동
cd yeobaek-python

# 서버 실행
uvicorn main:app --reload
```

서버가 실행되면 다음과 같이 표시됩니다:

```
INFO:     Uvicorn running on http://127.0.0.1:8000 (Press CTRL+C to quit)
INFO:     Started reloader process [xxxxx] using StatReload
INFO:     Started server process [xxxxx]
INFO:     Waiting for application startup.
INFO:     Application startup complete.
```

### 프로덕션 모드

```bash
uvicorn main:app --host 0.0.0.0 --port 8000 --workers 4
```

---

## 📡 API 사용법

### 1. 헬스 체크

```bash
curl http://localhost:8000/health
```

**응답:**
```json
{
  "status": "healthy"
}
```

### 2. API 문서 확인

- **Swagger UI**: http://localhost:8000/docs
- **ReDoc**: http://localhost:8000/redoc

### 3. 모델 학습 API

**Endpoint:** `POST /train`

**요청 예시:**
```bash
curl -X POST http://localhost:8000/train \
  -H "Content-Type: application/json" \
  -d '{
    "ratings": [
      {"userId": 1, "itemId": 101, "score": 4.5},
      {"userId": 1, "itemId": 102, "score": 3.0},
      {"userId": 2, "itemId": 101, "score": 5.0},
      {"userId": 2, "itemId": 103, "score": 4.0}
    ]
  }'
```

**응답 예시:**
```json
{
  "status": "success",
  "message": "Model trained successfully",
  "userCount": 10,
  "itemCount": 33
}
```

### 4. 추천 API

**Endpoint:** `POST /recommend`

**요청 예시:**
```bash
curl -X POST http://localhost:8000/recommend \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 5,
    "topN": 10
  }'
```

**응답 예시:**
```json
{
  "items": [106, 117, 129, 131, 132, 127, 107, 118, 130, 116]
}
```

---

## 📂 프로젝트 구조

```
yeobaek-python/
├── main.py                    # FastAPI 메인 애플리케이션
├── requirements.txt           # Python 패키지 의존성
├── README.md                  # 프로젝트 문서
├── schemas/                   # Pydantic 모델
│   ├── __init__.py
│   └── models.py             # 요청/응답 모델 정의
├── services/                  # 비즈니스 로직
│   ├── __init__.py
│   ├── als_trainer.py        # ALS 모델 학습
│   └── als_recommender.py    # ALS 추천 로직
└── models/                    # 학습된 모델 저장 (자동 생성)
    └── als_model.pkl         # 학습된 ALS 모델 파일
```

---

## 🔄 워크플로우

1. **백엔드에서 배치 작업으로 `/train` API 호출**
   - 전체 평점 데이터 전송
   - ALS 모델 학습 및 저장

2. **사용자 요청 시 `/recommend` API 호출**
   - userId 전달
   - 학습된 모델로 빠른 추천 반환

---

## 🛠️ VSCode에서 사용하기

### 1. Python 인터프리터 선택

1. `Ctrl + Shift + P`
2. "Python: Select Interpreter" 입력
3. `Python 3.12.x ('yeobaek')` 선택

### 2. VSCode 터미널 재시작

1. 기존 터미널 닫기 (휴지통 아이콘)
2. 새 터미널 열기: `Ctrl + ` ` (백틱)
3. 자동으로 conda 환경 활성화됨

### 3. 서버 실행

VSCode 터미널에서:
```bash
uvicorn main:app --reload
```

---

## ⚠️ 주의사항

### Windows 환경에서 implicit 설치 문제

`implicit` 패키지는 C++ 컴파일이 필요하므로 pip로 직접 설치 시 오류가 발생할 수 있습니다.

**해결 방법:**
```bash
# conda-forge 채널에서 설치 (권장)
conda install -c conda-forge implicit -y
```

### Conda 환경 활성화 확인

서버 실행 전 반드시 conda 환경이 활성화되어 있는지 확인하세요:

```bash
# 환경 목록 및 활성화 상태 확인 (* 표시가 현재 환경)
conda env list

# Python 경로 확인 (miniforge3/envs/yeobaek/python.exe 이어야 함)
python -c "import sys; print(sys.executable)"
```

---