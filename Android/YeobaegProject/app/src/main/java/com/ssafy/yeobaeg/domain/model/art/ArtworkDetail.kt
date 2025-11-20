package com.ssafy.yeobaeg.domain.model.art

data class ArtworkDetail(
    val artworkId: Long,
    val title: String,
    val category: String,
    val price: Int,
    val widthCm: Int,
    val heightCm: Int,
    val productionYear: Int,
    val weightKg: Double,
    val description: String,
    val viewCount: Int,
    val bookmarkCount: Int,
    val isBookmarked: Boolean,
    val images: List<String>,
    val artist: ArtistSummary,
    val exhibition: ExhibitionSpot?
)

data class ExhibitionSpot(
    val cafeId: Long,
    val cafeName: String,
    val cafeAddress: String,
    val cafeImageUrl: String,
    val slotName: String,
    val startDate: String,
    val endDate: String
)