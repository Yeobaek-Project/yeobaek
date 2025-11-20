package com.ssafy.yeobaeg.data.model.exhibition.response

import com.ssafy.yeobaeg.domain.model.exhibition.PossibleArtworkSummary
import kotlinx.serialization.Serializable

@Serializable
data class PossibleArtworkSummaryResponse(
    val artworkId: Long,
    val title: String,
    val thumbnailUrl: String,
    val widthCm: Double,
    val heightCm: Double,
    val weightKg: Double,
)

fun PossibleArtworkSummaryResponse.toDomain(): PossibleArtworkSummary {
    return PossibleArtworkSummary(
        artworkId = artworkId,
        title = title,
        thumbnailUrl = thumbnailUrl,
        widthCm = widthCm,
        heightCm = heightCm,
        weightKg = weightKg,
    )
}

fun List<PossibleArtworkSummaryResponse>.toDomain(): List<PossibleArtworkSummary> {
    return this.map { it.toDomain() }
}
