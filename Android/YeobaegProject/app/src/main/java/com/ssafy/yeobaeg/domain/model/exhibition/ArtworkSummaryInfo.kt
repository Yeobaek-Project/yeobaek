package com.ssafy.yeobaeg.domain.model.exhibition

data class ArtworkSummaryInfo(
    val artworkId: Long = 0L,
    val title: String = "",
    val widthCm: Double = 0.0,
    val heightCm: Double = 0.0,
    val weightKg: Double = 0.0,
    val artistName: String = "",
)