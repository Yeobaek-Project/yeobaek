"""
Database Package
MySQL 데이터베이스 연결 및 쿼리 함수 모듈
"""

from database.connection import (
    # 클래스
    Database,
    DatabaseConfig,

    # 작품 관련 함수
    get_artwork_with_embedding,
    get_all_available_artworks,

    # 전시벽 관련 함수
    get_display_walls,

    # 카페 카테고리 관련 함수
    get_cafe_categories,

    # 사용자 행동 데이터 관련 함수
    get_user_behavior_count,

    # 슬롯 관련 함수
    get_all_slots_for_recommendation,
    get_slot_details_for_recommendation
)

__all__ = [
    # 클래스
    'Database',
    'DatabaseConfig',

    # 작품 관련
    'get_artwork_with_embedding',
    'get_all_available_artworks',

    # 전시벽 관련
    'get_display_walls',

    # 카페 카테고리 관련
    'get_cafe_categories',

    # 사용자 행동 데이터 관련
    'get_user_behavior_count',

    # 슬롯 관련
    'get_all_slots_for_recommendation',
    'get_slot_details_for_recommendation'
]
