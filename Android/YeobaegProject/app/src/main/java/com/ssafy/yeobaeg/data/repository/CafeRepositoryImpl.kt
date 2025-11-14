package com.ssafy.yeobaeg.data.repository

import android.util.Log
import com.ssafy.yeobaeg.data.datasource.remote.CafeApiService
import com.ssafy.yeobaeg.data.model.cafe.toDomain
import com.ssafy.yeobaeg.data.model.request.UpdateSlotRequest
import com.ssafy.yeobaeg.data.model.request.toDto
import com.ssafy.yeobaeg.data.model.response.toDomain
import com.ssafy.yeobaeg.di.StoreJson
import com.ssafy.yeobaeg.domain.model.cafe.CafeDetail
import com.ssafy.yeobaeg.domain.model.cafe.CafeFilter
import com.ssafy.yeobaeg.domain.model.cafe.CafeListResult
import com.ssafy.yeobaeg.domain.model.DisplaySlot
import com.ssafy.yeobaeg.domain.model.cafe.CafeCategory
import com.ssafy.yeobaeg.domain.model.cafe.CafeSummary
import com.ssafy.yeobaeg.domain.model.cafe.RecommendedCafes
import com.ssafy.yeobaeg.domain.model.cafeowner.CafeOwnerInfo
import com.ssafy.yeobaeg.domain.model.cafeowner.RegisterCafe
import com.ssafy.yeobaeg.domain.repository.CafeRepository
import handleApi
import kotlinx.serialization.json.Json
import javax.inject.Inject

class CafeRepositoryImpl @Inject constructor(
    private val cafeApiService: CafeApiService,
    @StoreJson private val json: Json
) : CafeRepository {

    override suspend fun registerCafe(cafe: RegisterCafe): Result<Unit> = runCatching {
        handleApi(json) { cafeApiService.registerCafe(cafe.toDto()) }
    }

    override suspend fun getPopularCafes(size: Int): Result<List<CafeSummary>> = runCatching {
        val responseDto = handleApi(json) { cafeApiService.getPopularCafes(size) }
        responseDto.cafes.map { it.toDomain() }
    }

    override suspend fun registerCafeWall(registerWall: List<DisplaySlot>): Result<List<Unit>> = runCatching {
        handleApi(json) { cafeApiService.createCafeWall(registerWall.map { it.toDto() }) }
        emptyList()
    }

    override suspend fun getMyCafe(): Result<CafeOwnerInfo> = runCatching {
        val responseDto = handleApi(json) { cafeApiService.getMyCafe() }
        responseDto.myCafe.toDomain()
    }.recoverCatching { error ->
        if (error.message?.contains("CF4041") == true) {
            CafeOwnerInfo() // 특정 에러 코드일 경우, 성공으로 간주하고 기본 객체 반환
        } else {
            throw error // 그 외 에러는 다시 던져서 Result.failure로 처리
        }
    }

    override suspend fun updateSlotInfo(
        cafeId: Long,
        slotId: Long,
        commissionRate: Int?,
        requirements: String?
    ): Result<Unit> = runCatching {
        handleApi(json) {
            cafeApiService.updateSlotInfo(
                cafeId, slotId,
                UpdateSlotRequest(commissionRate, requirements)
            )
        }
    }

    override suspend fun getCafeCategories(): Result<List<CafeCategory>> = runCatching {
        val responseDto = handleApi(json) { cafeApiService.getCafeCategories() }
        responseDto.categories.map { it.toDomain() }
    }

    override suspend fun getCafes(
        keyword: String?,
        centerLatitude: Double,
        centerLongitude: Double,
        myLatitude: Double,
        myLongitude: Double,
        radiusKm: Double,
        filter: CafeFilter?,
        page: Int,
        size: Int
    ): Result<CafeListResult> = runCatching {
        val responseDto = handleApi(json) {
            cafeApiService.getCafes(
                keyword = keyword,
                centerLatitude = centerLatitude,
                centerLongitude = centerLongitude,
                myLatitude = myLatitude,
                myLongitude = myLongitude,
                radiusKm = radiusKm,
                minWidth = filter?.minWidth,
                maxWidth = filter?.maxWidth,
                minHeight = filter?.minHeight,
                maxHeight = filter?.maxHeight,
                minCommission = filter?.minCommission,
                maxCommission = filter?.maxCommission,
                page = page,
                size = size
            )
        }
        responseDto.toDomain()
    }

    override suspend fun getCafeDetail(cafeId: Long): Result<CafeDetail> = runCatching {
        val responseDto = handleApi(json) { cafeApiService.getCafeDetail(cafeId) }
        responseDto.cafeDetail.toDomain()
    }

    override suspend fun getRecommendedCafes(
        artworkId: Long,
        page: Int?,
        size: Int?
    ): Result<RecommendedCafes> = runCatching {
        val responseDto = handleApi(json) { cafeApiService.getRecommendedCafes(artworkId, page, size) }
        responseDto.toDomain()
    }

    override suspend fun toggleCafeBookmark(cafeId: Long): Result<Boolean> = runCatching {
        val responseDto = handleApi(json) { cafeApiService.toggleCafeBookmark(cafeId) }
        Log.d("CafeRepository", "toggleCafeBookmark response: $responseDto")
        responseDto.isBookmarked
    }
}