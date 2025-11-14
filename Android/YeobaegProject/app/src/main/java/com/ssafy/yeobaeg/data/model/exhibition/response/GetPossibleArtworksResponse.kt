package com.ssafy.yeobaeg.data.model.exhibition.response

import com.ssafy.yeobaeg.domain.model.exhibition.PossibleArtworks
import kotlinx.serialization.Serializable

@Serializable
data class GetPossibleArtworksResponse(
    val artworks: List<PossibleArtworkSummaryResponse>
)

fun GetPossibleArtworksResponse.toDomain(): PossibleArtworks {
    return PossibleArtworks(
        artworks = artworks.toDomain()
    )
}