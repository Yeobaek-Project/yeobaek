package com.ssafy.yeobaeg.domain.model.cafe

data class CafeSummary(
    val cafeId: Long,
    val cafeName: String,
    val address: String,
    val primaryImageUrl: String?,
    val availableSlotCount: Int,
    val totalSlotCount: Int,
    val distance: Double?,
    val categories: List<String>,
    val latitude: Double? = null,
    val longitude: Double? = null
)

