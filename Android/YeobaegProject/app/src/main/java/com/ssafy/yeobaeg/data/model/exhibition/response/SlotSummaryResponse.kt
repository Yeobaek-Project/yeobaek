package com.ssafy.yeobaeg.data.model.exhibition.response

import com.ssafy.yeobaeg.domain.model.exhibition.SlotSummaryInfo
import kotlinx.serialization.Serializable

@Serializable
data class SlotSummaryResponse(
    val slotId: Long,
    val wallName: String,
    val widthCm: Double,
    val heightCm: Double,
    val commissionRate: Double
)

fun SlotSummaryResponse.toDomain(): SlotSummaryInfo {
    return SlotSummaryInfo(
        slotId = slotId,
        wallName = wallName,
        widthCm = widthCm,
        heightCm = heightCm,
        commissionRate = commissionRate
    )
}