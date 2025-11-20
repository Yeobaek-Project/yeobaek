package com.ssafy.yeobaeg.data.model.profile

import kotlinx.serialization.Serializable

@Serializable
data class ArtworkSummaryResponse(
    val artworkId: Long,
    val title: String,
    val thumbnailUrl: String,
    val artist: ArtistInfoResponse? = null,
    val price: Int,
    val category: String,
    val widthCm: Int = 0,
    val heightCm: Int = 0,
    val isExhibiting: Boolean,
    val bookmarkCount: Int
)

@Serializable
data class ArtistInfoResponse(
    val userId: Long,
    val artistName: String,
    val profileImageUrl: String?
)