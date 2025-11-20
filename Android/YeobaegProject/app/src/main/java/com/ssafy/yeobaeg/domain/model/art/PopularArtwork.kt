package com.ssafy.yeobaeg.domain.model.art

import com.ssafy.yeobaeg.domain.model.SlotStatus

data class PopularArtwork(
    val artworkId: Long,
    val title: String,
    val thumbnailUrl: String,
    val artistName: String,
    val artistProfileImageUrl: String,
    val price: Int,
    val category: String,
    val widthCm: Int,
    val heightCm: Int,
    val status: SlotStatus,
    val bookmarkCount: Int,
    val cafeName: String? = null
)

