package com.ssafy.yeobaeg.domain.model.cafeowner

import com.ssafy.yeobaeg.domain.model.DisplaySlot

data class CafeOwnerInfo(
    val cafeId: Long = -1L,
    val cafeName: String = "test",
    val cafeImageUrl: String = "",
    val cafeAddress: String = "address",
    val numExhibitionSpaces: Int = 0,
    val numExhibitedWorks: Int = 0,
    val numArtistsWorkedWith: Int = 0,
    val displaySlots: List<DisplaySlot> = emptyList()
)