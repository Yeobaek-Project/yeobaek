package com.ssafy.yeobaeg.domain.model.exhibition

data class ExhibitionInfo(
    val exhibitionId: Long = 0L,
    val cafe: CafeSummaryInfo = CafeSummaryInfo(),
    val artwork: ArtworkSummaryInfo = ArtworkSummaryInfo(),
    val slot: SlotSummaryInfo = SlotSummaryInfo(),
    val startDate: String = "",
    val endDate: String = "",
    val commissionRate: Double = 0.0,
    val requirements: String = ""
)