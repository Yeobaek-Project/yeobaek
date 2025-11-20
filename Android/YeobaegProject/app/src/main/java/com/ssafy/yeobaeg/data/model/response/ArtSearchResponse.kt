package com.ssafy.yeobaeg.data.model.response

import com.ssafy.yeobaeg.data.model.PaginationResponse
import com.ssafy.yeobaeg.data.model.toDomain
import com.ssafy.yeobaeg.domain.model.SlotStatus
import com.ssafy.yeobaeg.domain.model.art.ArtSearchResult
import com.ssafy.yeobaeg.domain.model.art.ArtistSummary
import com.ssafy.yeobaeg.domain.model.profile.ArtworkSummary
import kotlinx.serialization.Serializable

@Serializable
data class ArtSearchResponse(
    val artists: List<Artist>,
    val artworks: List<ArtworkSummaryResponse>,
    val pagination: PaginationResponse
)

@Serializable
data class Artist(
    val artistName: String,
    val artworkCount: Int = 0,
    val introduction: String="",
    val profileImageUrl: String="",
    val userId: Long
)

@Serializable
data class ArtworkSummaryResponse(
    val artworkId: Long,
    val title: String,
    val thumbnailUrl: String? = null
)

fun ArtworkSummaryResponse.toDomain() = ArtworkSummary(
    artworkId = artworkId,
    title = title,
    thumbnailUrl = thumbnailUrl ?: ""
)
fun ArtSearchResponse.toDomain() = ArtSearchResult(
    artistSummaries = artists.map { it.toDomain() },
    artworkSummaries =  artworks.map { it.toDomain() },
    paginationInfo = pagination.toDomain()
)
fun Artist.toDomain() = ArtistSummary(
    artistId = userId,
    artistName = artistName,
    profileImageUrl = profileImageUrl,
    introduction = introduction,
    artworkCount = artworkCount
)