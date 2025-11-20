package com.ssafy.yeobaeg.domain.model.art

data class ArtistSummary(
    val artistId: Long,
    val artistName: String,
    val profileImageUrl: String,
    val introduction: String,
    val artworkCount: Int
)