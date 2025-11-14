package com.ssafy.yeobaeg.data.repository

import com.ssafy.yeobaeg.data.datasource.remote.ArtworkApiService
import com.ssafy.yeobaeg.data.model.art.request.toDto
import com.ssafy.yeobaeg.data.model.profile.toDomain
import com.ssafy.yeobaeg.data.model.response.toDomain
import com.ssafy.yeobaeg.di.StoreJson // Hilt Qualifier 어노테이션 import
import com.ssafy.yeobaeg.domain.model.artist.ArtHome
import com.ssafy.yeobaeg.domain.model.artist.ArtworkRegister
import com.ssafy.yeobaeg.domain.model.profile.Artwork
import com.ssafy.yeobaeg.domain.repository.ArtRepository
import handleApi // handleApi 함수 import
import kotlinx.serialization.json.Json // Json import
import javax.inject.Inject

class ArtRepositoryImpl @Inject constructor(
    private val artworkApiService: ArtworkApiService,
    @StoreJson private val json: Json // 1. Json 의존성 주입 추가
) : ArtRepository {

    override suspend fun getArtworks(
        category: List<String>?,
        minWidth: Int?,
        maxWidth: Int?,
        minHeight: Int?,
        maxHeight: Int?,
        page: Int,
        size: Int
    ) = runCatching {
        val responseDto = handleApi(json) {
            artworkApiService.getArtworks(
                categories = category?.joinToString(),
                minWidth = minWidth,
                maxWidth = maxWidth,
                minHeight = minHeight,
                maxHeight = maxHeight,
                page = page,
                size = size
            )
        }
        responseDto.toDomain()
    }

    override suspend fun searchArt(
        keyword: String,
        categories: List<String>?,
        minWidth: Int?,
        maxWidth: Int?,
        minHeight: Int?,
        maxHeight: Int?,
        sortBy: String,
        page: Int,
        size: Int
    ) = runCatching {
        val responseDto = handleApi(json) {
            artworkApiService.getArtworks(
                keyword = keyword,
                categories = categories?.joinToString(),
                minWidth = minWidth,
                maxWidth = maxWidth,
                minHeight = minHeight,
                maxHeight = maxHeight,
                sortBy = sortBy,
                page = page,
                size = size
            )
        }
        responseDto.toDomain()
    }

    override suspend fun getArtworkDetail(artWorkId: Long) = runCatching {
        val responseDto = handleApi(json) { artworkApiService.getArtworkDetail(artWorkId) }
        responseDto.artwork.toDomain()
    }

    override suspend fun toggleBookmark(artWorkId: Long) = runCatching {
        val responseDto = handleApi(json) { artworkApiService.toggleBookmark(artWorkId) }
        responseDto.isBookmarked
    }

    override suspend fun getPopularArtworks(size: Int) = runCatching {
        val responseDto = handleApi(json) { artworkApiService.getPopularArtworks(size) }
        responseDto.artworks.map { it.toDomain() }
    }

    // 원본 코드의 반환 타입이 Unit이었으나, Result<Unit>으로 변경하여 성공/실패 여부를 명확히 전달
    override suspend fun registerArtwork(request: ArtworkRegister) = runCatching {
        handleApi(json) { artworkApiService.registerArtwork(request.toDto()) }
    }

    override suspend fun getArtHome(): Result<ArtHome> = runCatching {
        val responseDto = handleApi(json) { artworkApiService.getMyArtHome() }
        responseDto.toDomain()
    }

    override suspend fun getMyArtworks(page: Int?, size: Int?): Result<Artwork> = runCatching {
        val responseDto = handleApi(json) { artworkApiService.getMyArtworks(page, size) }
        responseDto.toDomain()
    }

    override suspend fun getRecommendedArtworks(
        slotId: Long,
        page: Int?,
        size: Int?
    ): Result<Artwork> = runCatching {
        val responseDto = handleApi(json) { artworkApiService.getRecommendedArtworks(slotId, page, size) }
        responseDto.toDomain()
    }
}