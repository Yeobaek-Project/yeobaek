package com.ssafy.yeobaeg.data.model.cafe

import com.ssafy.yeobaeg.domain.model.cafe.CafeSummary
import kotlinx.serialization.Serializable

@Serializable
data class CafeSummaryResponse(
    val cafeId: Long,
    val cafeName: String,
    val address: String,
    val primaryImageUrl: String? = null,
    val availableSlotCount: Int,
    val totalSlotCount: Int,
    val distance: Double,
    val categories: List<String> = emptyList(),
    val latitude: Double? = null,
    val longitude: Double? = null
)

fun CafeSummaryResponse.toDomain(): CafeSummary {
    return CafeSummary(
        cafeId = cafeId,
        cafeName = cafeName,
        address = address,
        primaryImageUrl = primaryImageUrl,
        availableSlotCount = availableSlotCount,
        totalSlotCount = totalSlotCount,
        distance = distance,
        categories = categories,
        latitude = latitude,
        longitude = longitude
    )
}

