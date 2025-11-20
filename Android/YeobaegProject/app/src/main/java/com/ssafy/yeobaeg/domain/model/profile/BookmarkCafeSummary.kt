package com.ssafy.yeobaeg.domain.model.profile

import kotlinx.serialization.Serializable

@Serializable
data class BookmarkCafeSummary(
    val cafeId: Long,
    val address: String,
    val primaryImageUrl: String,
    val cafeName: String,
    val categories: List<String>,
    val availableSlotCount: Int,
    val totalSlotCount: Int,
)