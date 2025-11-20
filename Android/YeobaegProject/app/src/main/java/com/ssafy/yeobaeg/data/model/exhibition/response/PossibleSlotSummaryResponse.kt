package com.ssafy.yeobaeg.data.model.exhibition.response

import com.ssafy.yeobaeg.domain.model.exhibition.PossibleSlotSummary
import kotlinx.serialization.Serializable

@Serializable
data class PossibleSlotSummaryResponse(
    val wallId: Long,
    val wallName: String,
    val width: Double,
    val height: Double,
    val commissionRate: Double,
    val requirements: String?=null,
)

fun PossibleSlotSummaryResponse.toDomain(): PossibleSlotSummary {
    return PossibleSlotSummary(
        wallId = wallId,
        wallName = wallName,
        width = width,
        height = height,
        commissionRate = commissionRate,
        requirements = requirements
    )
}

fun List<PossibleSlotSummaryResponse>.toDomain(): List<PossibleSlotSummary> {
    return this.map { it.toDomain() }
}
