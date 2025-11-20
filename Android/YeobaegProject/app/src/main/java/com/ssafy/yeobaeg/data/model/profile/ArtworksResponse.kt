package com.ssafy.yeobaeg.data.model.profile

import com.ssafy.yeobaeg.data.model.PaginationResponse
import com.ssafy.yeobaeg.data.model.toDomain
import com.ssafy.yeobaeg.domain.model.profile.ArtworkSummary
import com.ssafy.yeobaeg.domain.model.profile.Artwork
import kotlinx.serialization.Serializable

@Serializable
data class ArtworksResponse(
    val artworks: List<ArtworkSummaryResponse>,
)

fun ArtworksResponse.toDomain(): Artwork {
    return Artwork(
        artworks = artworks.map { it.toDomain() },
    )
}

fun ArtworkSummaryResponse.toDomain(): ArtworkSummary {
    return ArtworkSummary(
        artworkId = artworkId,
        title = title,
        thumbnailUrl = thumbnailUrl,
        artistName = artist?.artistName ?: "",
        artistProfileImageUrl = artist?.profileImageUrl ?: "",
        price = price,
        category = when (category) {
            "LANDSCAPE"  -> "풍경화"
            "PORTRAIT"   -> "인물화"
            "STILL_LIFE" -> "정물화"
            "HISTORY" -> "역사화"
            "GENRE"      -> "장르화"
            else     -> "기타"
        },
        widthCm = widthCm,
        heightCm = heightCm,
        isExhibiting = isExhibiting,
        bookmarkCount = bookmarkCount
    )
}
