package com.ssafy.yeobaeg.domain.model.exhibition

data class SlotSummaryInfo(
    val slotId: Long = 0L,
    val wallName: String = "",
    val widthCm: Double = 0.0,
    val heightCm: Double = 0.0,
    val commissionRate: Double = 0.0
)