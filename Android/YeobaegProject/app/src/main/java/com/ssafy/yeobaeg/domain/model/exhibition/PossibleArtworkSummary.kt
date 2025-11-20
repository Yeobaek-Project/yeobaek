package com.ssafy.yeobaeg.domain.model.exhibition

data class PossibleArtworkSummary(
    val artworkId: Long,
    val title: String,
    val thumbnailUrl:String,
    val widthCm: Double,
    val heightCm: Double,
    val weightKg: Double,
)