package com.ssafy.yeobaeg.domain.model.artist

data class ArtHome(
    val artist: ArtistHome,
    val artworks: List<ArtworkHome>
)

data class ArtistHome(
    val artistName: String,
    val artworkCount: Int,
    val currentExhibitionCount: Int,
    val profileImageUrl: String,
    val userId: Int
)

data class ArtworkHome(
    val artworkId: Int,
    val bookmarkCount: Int,
    val cafeName: String?,
    val category: String,
    val isExhibiting: Boolean,
    val price: Int,
    val thumbnailUrl: String?,
    val title: String
)
