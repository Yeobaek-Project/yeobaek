"""
MySQL Database Connection Module
읽기 전용 연결을 통해 작품, 카페, 임베딩 데이터 조회
"""

import os
import json
from typing import Optional, List, Dict, Any
from contextlib import contextmanager
import mysql.connector
from mysql.connector import pooling
from dotenv import load_dotenv

# 환경 변수 로드
load_dotenv()


class DatabaseConfig:
    """데이터베이스 연결 설정"""

    HOST = os.getenv("DB_HOST")
    PORT = int(os.getenv("DB_PORT"))
    DATABASE = os.getenv("DB_NAME")
    USER = os.getenv("DB_USER")
    PASSWORD = os.getenv("DB_PASSWORD")

    # 연결 풀 설정
    POOL_NAME = "yeobaek_pool"
    POOL_SIZE = 5


class Database:
    """MySQL 데이터베이스 연결 관리 (읽기 전용)"""

    _connection_pool = None

    @classmethod
    def get_pool(cls):
        """연결 풀 가져오기 (싱글톤)"""
        if cls._connection_pool is None:
            cls._connection_pool = pooling.MySQLConnectionPool(
                pool_name=DatabaseConfig.POOL_NAME,
                pool_size=DatabaseConfig.POOL_SIZE,
                host=DatabaseConfig.HOST,
                port=DatabaseConfig.PORT,
                database=DatabaseConfig.DATABASE,
                user=DatabaseConfig.USER,
                password=DatabaseConfig.PASSWORD,
                charset='utf8mb4',
                collation='utf8mb4_unicode_ci'
            )
        return cls._connection_pool

    @classmethod
    @contextmanager
    def get_connection(cls):
        """연결 풀에서 연결 가져오기 (컨텍스트 매니저)"""
        pool = cls.get_pool()
        connection = pool.get_connection()
        try:
            yield connection
        finally:
            connection.close()

    @classmethod
    def execute_query(cls, query: str, params: tuple = None) -> List[Dict[str, Any]]:
        """
        SELECT 쿼리 실행

        Args:
            query: SQL 쿼리
            params: 쿼리 파라미터

        Returns:
            결과 리스트 (딕셔너리 형태)
        """
        with cls.get_connection() as conn:
            cursor = conn.cursor(dictionary=True)
            cursor.execute(query, params or ())
            results = cursor.fetchall()
            cursor.close()
            return results

    @classmethod
    def parse_embedding(cls, embedding_str: Optional[str]) -> Optional[List[float]]:
        """
        JSON 문자열 임베딩을 float 리스트로 변환

        Args:
            embedding_str: JSON 문자열 형태의 임베딩 "[0.1, 0.2, ...]"

        Returns:
            float 리스트 또는 None
        """
        if not embedding_str:
            return None
        try:
            return json.loads(embedding_str)
        except json.JSONDecodeError:
            return None


# 작품(Artwork) 관련 함수

def get_artwork_with_embedding(artwork_id: int) -> Optional[Dict[str, Any]]:
    """
    작품 정보와 임베딩 조회

    Args:
        artwork_id: 작품 ID

    Returns:
        작품 정보 딕셔너리 (embedding은 float 리스트로 파싱됨)
    """
    query = """
        SELECT
            id, art_category, width_cm, height_cm, description_embedding, artist_id
        FROM artwork
        WHERE id = %s
    """
    results = Database.execute_query(query, (artwork_id,))

    if not results:
        return None

    artwork = results[0]
    artwork['embedding'] = Database.parse_embedding(artwork.get('description_embedding'))
    return artwork


def get_all_available_artworks() -> List[Dict[str, Any]]:
    """
    전시 가능한 모든 작품 조회 (is_exhibiting = false)

    Returns:
        작품 정보 리스트
    """
    query = """
        SELECT
            id, art_category, width_cm, height_cm, description_embedding
        FROM artwork
        WHERE is_exhibiting = false
    """
    results = Database.execute_query(query)

    # 각 작품의 임베딩 파싱
    for artwork in results:
        artwork['embedding'] = Database.parse_embedding(artwork.get('description_embedding'))

    return results


# 전시벽(DisplayWall) 관련 함수

def get_display_walls(cafe_id: int) -> List[Dict[str, Any]]:
    """
    카페의 전시벽 정보 조회

    Args:
        cafe_id: 카페 ID

    Returns:
        전시벽 정보 리스트 (width_cm, height_cm 포함)
    """
    query = """
        SELECT
            width_cm, height_cm
        FROM display_wall
        WHERE cafe_id = %s
    """
    return Database.execute_query(query, (cafe_id,))


# 카페 카테고리 관련 함수

def get_cafe_categories(cafe_id: int) -> List[str]:
    """
    카페의 카테고리(분위기/스타일) 조회

    Args:
        cafe_id: 카페 ID

    Returns:
        카테고리 문자열 리스트 (예: ["자연친화적", "아늑한"])
    """
    query = """
        SELECT T2.category_name
        FROM cafe_category_tag AS T1
        JOIN cafe_category AS T2 ON T1.category_id = T2.id
        WHERE T1.cafe_id = %s
    """
    results = Database.execute_query(query, (cafe_id,))
    return [row['category_name'] for row in results]


# 사용자 행동 데이터 관련 함수

def get_user_behavior_count(user_id: int) -> int:
    """
    사용자의 행동 데이터 개수 조회 (하이브리드 가중치 결정용)

    Args:
        user_id: 사용자 ID

    Returns:
        행동 데이터 개수
    """
    query = """
        SELECT COUNT(*) as count
        FROM user_behavior_event
        WHERE user_id = %s
    """
    results = Database.execute_query(query, (user_id,))
    return results[0]['count'] if results else 0


# 슬롯(Slot) 관련 함수

def get_all_slots_for_recommendation() -> List[Dict[str, Any]]:
    """
    추천 계산에 필요한 모든 슬롯 정보를 조회합니다.

    Returns:
        모든 슬롯의 상세 정보 리스트
    """
    query = """
        SELECT
            ds.id,
            ds.requirements_embedding,
            dw.width_cm,
            dw.height_cm,
            dw.cafe_id
        FROM display_slot AS ds
        JOIN display_wall AS dw ON ds.id = dw.id
    """
    results = Database.execute_query(query)
    for slot in results:
        slot['embedding'] = Database.parse_embedding(slot.get('requirements_embedding'))
    return results


def get_slot_details_for_recommendation(slot_id: int) -> Optional[Dict[str, Any]]:
    """
    추천 계산에 필요한 슬롯 및 관련 카페 정보를 조회

    Args:
        slot_id: 조회할 슬롯(display_slot)의 ID

    Returns:
        슬롯, 벽, 카페 카테고리, 카페 소유자 ID 정보가 포함된 딕셔너리 또는 None
    """
    query = """
        SELECT
            ds.id,
            ds.requirements_embedding,
            dw.width_cm,
            dw.height_cm,
            dw.cafe_id,
            c.owner_id
        FROM display_slot AS ds
        JOIN display_wall AS dw ON ds.id = dw.id
        JOIN cafe AS c ON dw.cafe_id = c.id
        WHERE ds.id = %s
    """
    results = Database.execute_query(query, (slot_id,))

    if not results:
        return None

    slot_details = results[0]

    # 임베딩 파싱
    slot_details['embedding'] = Database.parse_embedding(slot_details.get('requirements_embedding'))

    # 카페 카테고리 정보 추가
    cafe_id = slot_details.get('cafe_id')
    if cafe_id:
        slot_details['categories'] = get_cafe_categories(cafe_id)
    else:
        slot_details['categories'] = []

    return slot_details
