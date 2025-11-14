package com.ssafy.yeobaeg.domain.repository

import com.ssafy.yeobaeg.domain.model.DisplaySlot
import com.ssafy.yeobaeg.domain.model.cafe.CafeCategory
import com.ssafy.yeobaeg.domain.model.cafe.CafeDetail
import com.ssafy.yeobaeg.domain.model.cafe.CafeFilter
import com.ssafy.yeobaeg.domain.model.cafe.CafeListResult
import com.ssafy.yeobaeg.domain.model.cafe.CafeSummary
import com.ssafy.yeobaeg.domain.model.cafe.RecommendedCafes
import com.ssafy.yeobaeg.domain.model.cafeowner.CafeOwnerInfo
import com.ssafy.yeobaeg.domain.model.cafeowner.RegisterCafe

interface CafeRepository {

    suspend fun registerCafe(cafe: RegisterCafe): Result<Unit>

    suspend fun getPopularCafes(size: Int = 10): Result<List<CafeSummary>>

    /**
     * 카페 목록 조회
     *
     * @param keyword 카페명 검색 키워드
     * @param centerLatitude 지도 중심 위도
     * @param centerLongitude 지도 중심 경도
     * @param myLatitude 내 위치 위도
     * @param myLongitude 내 위치 경도
     * @param radiusKm 반경 km
     * @param filter 카페 필터 (작가 전용)
     * @param page 페이지 번호
     * @param size 페이지 크기
     */
    suspend fun getCafes(
        keyword: String? = null,
        centerLatitude: Double,
        centerLongitude: Double,
        myLatitude: Double,
        myLongitude: Double,
        radiusKm: Double,
        filter: CafeFilter? = null,
        page: Int = 0,
        size: Int = 20
    ): Result<CafeListResult>

    /**
     * 카페 상세 정보 조회
     *
     * @param cafeId 카페 ID
     */
    suspend fun getCafeDetail(cafeId: Long): Result<CafeDetail>

    suspend fun registerCafeWall(registerWall: List<DisplaySlot>): Result<List<Unit>>

    suspend fun getMyCafe(): Result<CafeOwnerInfo>

    suspend fun updateSlotInfo(
        cafeId: Long,
        slotId: Long,
        commissionRate: Int?,
        requirements: String?
    ): Result<Unit>

    suspend fun getCafeCategories(): Result<List<CafeCategory>>

    suspend fun getRecommendedCafes(
        artworkId: Long,
        page: Int? = null,
        size: Int? = null
    ): Result<RecommendedCafes>

    /**
     * 카페 북마크 토글
     *
     * @param cafeId 카페 ID
     * @return 북마크 상태 (true: 북마크됨, false: 북마크 해제됨)
     */
    suspend fun toggleCafeBookmark(cafeId: Long): Result<Boolean>
}