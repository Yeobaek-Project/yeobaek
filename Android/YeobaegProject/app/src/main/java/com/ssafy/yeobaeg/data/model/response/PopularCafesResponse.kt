package com.ssafy.yeobaeg.data.model.response

import com.ssafy.yeobaeg.domain.model.cafe.CafeSummary
import kotlinx.serialization.Serializable

@Serializable
data class PopularCafesData(
    val cafes: List<PopularCafeDto>
)

@Serializable
data class PopularCafeDto(
    val cafeId: Long,
    val cafeName: String,
    val address: String,
    val primaryImageUrl: String,
    val availableSlotCount: Int,
    val totalSlotCount: Int,
    val distance: Double? = null,
    val categories: List<String>
)

fun PopularCafeDto.toDomain() = CafeSummary(
    cafeId = cafeId,
    cafeName = cafeName,
    address = address,
    primaryImageUrl = primaryImageUrl,
    availableSlotCount = availableSlotCount,
    totalSlotCount = totalSlotCount,
    distance = distance,
    categories = categories
)

