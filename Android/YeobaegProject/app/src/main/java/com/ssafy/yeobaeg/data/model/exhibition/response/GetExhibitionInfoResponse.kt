package com.ssafy.yeobaeg.data.model.exhibition.response

import com.ssafy.yeobaeg.domain.model.exhibition.ExhibitionInfo
import kotlinx.serialization.Serializable

@Serializable
data class GetExhibitionInfoResponse(
    val exhibitionId: Long,
    val cafe: CafeSummaryResponse,
    val artwork: ArtworkSummaryResponse,
    val startDate: String,
    val endDate: String,
    val commissionRate: Double,
    val requirements: String
)

fun GetExhibitionInfoResponse.toDomain(): ExhibitionInfo {
    return ExhibitionInfo(
        exhibitionId = exhibitionId,
        cafe = cafe.toDomain(),
        artwork = artwork.toDomain(),
        startDate = startDate,
        endDate = endDate,
        commissionRate = commissionRate,
        requirements = requirements
    )
}