package com.ssafy.yeobaeg.domain.repository

import com.ssafy.yeobaeg.domain.model.art.ArtSearchResult
import com.ssafy.yeobaeg.domain.model.art.ArtworkDetail
import com.ssafy.yeobaeg.domain.model.artist.ArtworkRegister
import com.ssafy.yeobaeg.domain.model.art.PopularArtwork
import com.ssafy.yeobaeg.domain.model.artist.ArtHome
import com.ssafy.yeobaeg.domain.model.profile.Artwork

interface ArtRepository {

    suspend fun getArtworks(
        category: List<String>?,
        minWidth: Int? = null,
        maxWidth: Int? = null,
        minHeight: Int? = null,
        maxHeight: Int? = null,
        page: Int, size: Int
    ): Result<ArtSearchResult>

    suspend fun searchArt(
        keyword: String = "",
        categories: List<String>? = null,
        minWidth: Int? = null,
        maxWidth: Int? = null,
        minHeight: Int? = null,
        maxHeight: Int? = null,
        sortBy: String = "LATEST",
        page: Int = 0,
        size: Int = 20
    ): Result<ArtSearchResult>

    suspend fun getArtworkDetail(artWorkId: Long): Result<ArtworkDetail>

    suspend fun toggleBookmark(artWorkId: Long): Result<Boolean>

    suspend fun getPopularArtworks(size: Int = 10): Result<List<PopularArtwork>>
    suspend fun registerArtwork(request: ArtworkRegister): Result<String>

    suspend fun getArtHome(): Result<ArtHome>

    suspend fun getMyArtworks(page: Int? = null, size: Int? = null): Result<Artwork>

    suspend fun getRecommendedArtworks(slotId: Long, page: Int? = null, size: Int? = null): Result<Artwork>
}