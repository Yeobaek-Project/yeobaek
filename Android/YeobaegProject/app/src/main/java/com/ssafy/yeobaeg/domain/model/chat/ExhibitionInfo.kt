package com.ssafy.yeobaeg.domain.model.chat

data class ExhibitionInfo(
    val exhibitionId: Long,
    val cafeName: String,
    val slotName: String,
    val artworkTitle:String,
    val exhibitionStartDate: String,
    val exhibitionEndDate: String,
    val commissionRate: Double,
    val requirements: String,
)