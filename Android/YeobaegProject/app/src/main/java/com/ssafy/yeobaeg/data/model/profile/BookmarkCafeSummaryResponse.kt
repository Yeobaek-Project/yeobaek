package com.ssafy.yeobaeg.data.model.profile

import kotlinx.serialization.Serializable

@Serializable
data class BookmarkCafeSummaryResponse(
    val cafeId: Long,
    val address: String,
    val primaryImageUrl: String,
    val cafeName: String,
    val categories: List<String>,
    val availableSlotCount: Int,
    val totalSlotCount: Int,
)