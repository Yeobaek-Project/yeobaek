package com.ssafy.yeobaeg.domain.model.exhibition

data class ArtworkSummaryInfo(
    val artworkId: Long,
    val title: String,
    val widthCm: Double,
    val heightCm: Double,
    val weightKg: Double,
    val artistName: String,
)