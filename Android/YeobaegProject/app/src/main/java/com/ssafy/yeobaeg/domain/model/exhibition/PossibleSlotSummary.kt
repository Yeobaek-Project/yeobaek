package com.ssafy.yeobaeg.domain.model.exhibition

data class PossibleSlotSummary(
    val wallId: Long,
    val wallName: String,
    val width: Double,
    val height: Double,
    val commissionRate: Double,
    val requirements: String?=null,
)