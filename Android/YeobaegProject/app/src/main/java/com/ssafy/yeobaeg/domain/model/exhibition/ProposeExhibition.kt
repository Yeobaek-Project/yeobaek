package com.ssafy.yeobaeg.domain.model.exhibition


data class ProposeExhibition(
    val cafeId: Long = 0L,
    val artworkId: Long = 0L,
    val slotId: Long = 0L,
    val startDate: String = "",
    val endDate: String = "",
    val commissionRate: Double = 0.0,
    val requirements: String = ""
)