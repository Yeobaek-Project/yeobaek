package com.ssafy.yeobaeg.domain.model.exhibition

data class ExhibitionInfo(
    val exhibitionId: Long,
    val cafe: CafeSummaryInfo,
    val artwork: ArtworkSummaryInfo,
    val startDate: String,
    val endDate: String,
    val commissionRate: Double,
    val requirements: String
)