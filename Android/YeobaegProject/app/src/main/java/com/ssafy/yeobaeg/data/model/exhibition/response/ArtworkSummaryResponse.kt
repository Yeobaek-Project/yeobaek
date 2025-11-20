package com.ssafy.yeobaeg.data.model.exhibition.response

import com.ssafy.yeobaeg.domain.model.exhibition.ArtworkSummaryInfo
import kotlinx.serialization.Serializable

@Serializable
data class ArtworkSummaryResponse(
    val artworkId: Long,
    val title: String,
    val widthCm: Double,
    val heightCm: Double,
    val weightKg: Double,
    val artistName: String,
)

fun ArtworkSummaryResponse.toDomain(): ArtworkSummaryInfo {
    return ArtworkSummaryInfo(
        artworkId = artworkId,
        title = title,
        widthCm = widthCm,
        heightCm = heightCm,
        weightKg = weightKg,
        artistName = artistName
    )
}