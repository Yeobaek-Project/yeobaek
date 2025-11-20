package com.ssafy.yeobaeg.data.datasource.remote

import com.ssafy.yeobaeg.data.model.BaseResponse
import com.ssafy.yeobaeg.data.model.cafe.CafeDetailData
import com.ssafy.yeobaeg.data.model.cafe.CafeListResponse
import com.ssafy.yeobaeg.data.model.request.CreateDisplayWallRequest
import com.ssafy.yeobaeg.data.model.request.RegisterCafeRequest
import com.ssafy.yeobaeg.data.model.request.UpdateSlotRequest
import com.ssafy.yeobaeg.data.model.response.CafeCategoryResponse
import com.ssafy.yeobaeg.data.model.response.CafeOwnerHomeResponse
import com.ssafy.yeobaeg.data.model.response.PopularCafesData
import com.ssafy.yeobaeg.data.model.response.RecommendedCafesResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface CafeApiService {

    @POST("cafes")
    suspend fun registerCafe(
        @Body request: RegisterCafeRequest
    ) : BaseResponse<Unit>

    @GET("cafes/popular")
    suspend fun getPopularCafes(
        @Query("size") size: Int = 10
    ): BaseResponse<PopularCafesData>

    @GET("cafes/me")
    suspend fun getMyCafe(): BaseResponse<CafeOwnerHomeResponse>

    @POST("cafes/walls")
    suspend fun createCafeWall(
        @Body request: List<CreateDisplayWallRequest>
    ): BaseResponse<List<Unit>>

    @PATCH("cafes/{cafeId}/slots/{slotId}")
    suspend fun updateSlotInfo(
        @Path("cafeId") cafeId: Long,
        @Path("slotId") slotId: Long,
        @Body request: UpdateSlotRequest,
    ): BaseResponse<Unit>

    @GET("cafes/categories")
    suspend fun getCafeCategories(): BaseResponse<CafeCategoryResponse>

    /**
     * 카페 목록 조회
     *
     * @param keyword 카페명 검색 키워드
     * @param centerLatitude 지도 중심 위도 (필수)
     * @param centerLongitude 지도 중심 경도 (필수)
     * @param myLatitude 내 위치 위도 (필수)
     * @param myLongitude 내 위치 경도 (필수)
     * @param radiusKm 반경 몇 km 이내 (필수)
     * @param minWidth 슬롯 최소 폭 (cm) - 작가만
     * @param maxWidth 슬롯 최대 폭 (cm) - 작가만
     * @param minHeight 슬롯 최소 높이 (cm) - 작가만
     * @param maxHeight 슬롯 최대 높이 (cm) - 작가만
     * @param minCommission 최소 수수료율 (%) - 작가만
     * @param maxCommission 최대 수수료율 (%) - 작가만
     * @param page 페이지 번호 (0부터 시작, 기본값: 0)
     * @param size 페이지 크기 (기본값: 20, 최대: 50)
     */
    @GET("cafes")
    suspend fun getCafes(
        @Query("keyword") keyword: String? = null,
        @Query("centerLatitude") centerLatitude: Double,
        @Query("centerLongitude") centerLongitude: Double,
        @Query("myLatitude") myLatitude: Double,
        @Query("myLongitude") myLongitude: Double,
        @Query("radiusKm") radiusKm: Double,
        @Query("minWidth") minWidth: Int? = null,
        @Query("maxWidth") maxWidth: Int? = null,
        @Query("minHeight") minHeight: Int? = null,
        @Query("maxHeight") maxHeight: Int? = null,
        @Query("minCommission") minCommission: Double? = null,
        @Query("maxCommission") maxCommission: Double? = null,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20
    ): BaseResponse<CafeListResponse>

    /**
     * 카페 상세 정보 조회
     *
     * @param cafeId 카페 ID
     */
    @GET("cafes/{cafeId}")
    suspend fun getCafeDetail(
        @Path("cafeId") cafeId: Long
    ): BaseResponse<CafeDetailData>

    @GET("cafes/recommendations")
    suspend fun getRecommendedCafes(
        @Query("artworkId") artworkId: Long,
    ): BaseResponse<RecommendedCafesResponse>

    /**
     * 카페 북마크 토글
     *
     * @param cafeId 카페 ID
     */
    @POST("cafes/{cafeId}/bookmarks")
    suspend fun toggleCafeBookmark(
        @Path("cafeId") cafeId: Long
    ): BaseResponse<com.ssafy.yeobaeg.data.model.response.ToggleBookmarkResponse>
}
