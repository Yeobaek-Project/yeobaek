package com.ssafy.yeobaeg.domain.model.chat

import kotlinx.serialization.Serializable

@Serializable
data class ExhibitionInfo(
    val exhibitionId: Long = 0L,
    val cafeName: String = "",
    val slotName: String = "",
    val artworkTitle: String = "",
    val exhibitionStartDate: String = "",
    val exhibitionEndDate: String = "",
    val commissionRate: Double = 0.0,
    val requirements: String = "",
)