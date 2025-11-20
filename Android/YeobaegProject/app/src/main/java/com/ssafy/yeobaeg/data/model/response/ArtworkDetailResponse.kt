package com.ssafy.yeobaeg.data.model.response

import com.ssafy.yeobaeg.domain.model.art.ArtistSummary
import com.ssafy.yeobaeg.domain.model.art.ArtworkDetail
import com.ssafy.yeobaeg.domain.model.art.ExhibitionSpot
import kotlinx.serialization.Serializable
import kotlin.collections.map

@Serializable
data class ArtworkDetailResponse(
    val artwork: ArtworkDetailData
)
@Serializable
data class ArtworkDetailData(
    val artist: Artist,
    val artworkId: Long,
    val bookmarkCount: Int,
    val category: String,
    val currentExhibition: CurrentExhibition? = null,
    val description: String,
    val heightCm: Int,
    val images: List<String>,
    val isBookmarked: Boolean,
    val price: Int,
    val productionYear: Int,
    val isExhibiting: Boolean,
    val title: String,
    val viewCount: Int,
    val weightKg: Double,
    val widthCm: Int
)

@Serializable
data class CurrentExhibition(
    val cafeAddress: String,
    val cafeId: Long,
    val cafeImageUrl: String,
    val cafeName: String,
    val exhibitionEndDate: String,
    val exhibitionStartDate: String,
    val slotName: String? = null
)

fun ArtworkDetailData.toDomain(): ArtworkDetail = ArtworkDetail(
    artworkId = artworkId,
    title = title,
    category = when (category) {
        "LANDSCAPE"  -> "풍경화"
        "PORTRAIT"   -> "인물화"
        "STILL_LIFE" -> "정물화"
        "HISTORY" -> "역사화"
        "GENRE"      -> "장르화"
        else     -> "기타"
    },
    price = price,
    widthCm = widthCm,
    heightCm = heightCm,
    productionYear = productionYear,
    weightKg = weightKg,
    description = description,
    viewCount = viewCount,
    bookmarkCount = bookmarkCount,
    isBookmarked = isBookmarked,
    images = images,
    artist = ArtistSummary(
        artistId = artist.userId,
        artistName = artist.artistName,
        introduction = artist.introduction,
        profileImageUrl = artist.profileImageUrl,
        artworkCount = 0
    ),
    exhibition = currentExhibition?.let {
        ExhibitionSpot(
            cafeId = it.cafeId,
            cafeName = it.cafeName,
            cafeAddress = it.cafeAddress,
            cafeImageUrl = it.cafeImageUrl,
            slotName = it.slotName ?: "슬롯 정보 없음",
            startDate = it.exhibitionStartDate,
            endDate = it.exhibitionEndDate
        )
    }
)