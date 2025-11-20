package com.ssafy.yeobaeg.domain.model.profile

data class ArtworkSummary(
    val artworkId: Long,
    val title: String = "",
    val thumbnailUrl: String = "",
    val artistName: String = "",
    val artistProfileImageUrl: String? = null,
    val price: Int = 0,
    val category: String = "",
    val widthCm: Int = 0,
    val heightCm: Int = 0,
    val isExhibiting: Boolean = false,
    val bookmarkCount: Int = 0
)