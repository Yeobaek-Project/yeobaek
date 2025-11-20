package com.ssafy.yeobaeg.data.model.response

import com.ssafy.yeobaeg.domain.model.SlotStatus
import com.ssafy.yeobaeg.domain.model.art.PopularArtwork
import kotlinx.serialization.Serializable

@Serializable
data class PopularArtworksData(
    val artworks: List<PopularArtworkDto>
)

@Serializable
data class PopularArtworkDto(
    val artworkId: Long,
    val title: String,
    val thumbnailUrl: String,
    val price: Int,
    val category: String,
    val isExhibiting: Boolean? = null,
    val bookmarkCount: Int,
    val cafeName: String? = null,
    val artist: ArtistDto? = null
)

@Serializable
data class ArtistDto(
    val userId: Long,
    val artistName: String,
    val profileImageUrl: String? = null
)

fun PopularArtworkDto.toDomain() = PopularArtwork(
    artworkId = artworkId,
    title = title,
    thumbnailUrl = thumbnailUrl,
    artistName = artist?.artistName ?: "알 수 없음",
    artistProfileImageUrl = artist?.profileImageUrl ?: "",
    price = price,
    category = when (category) {
        "LANDSCAPE"  -> "풍경화"
        "PORTRAIT"   -> "인물화"
        "STILL_LIFE" -> "정물화"
        "HISTORY" -> "역사화"
        "GENRE"      -> "장르화"
        else     -> "기타"
    },
    widthCm = 0,
    heightCm = 0,
    status = when (isExhibiting) {
        true -> SlotStatus.EXHIBITING
        false -> SlotStatus.AVAILABLE
        null -> SlotStatus.AVAILABLE
    },
    bookmarkCount = bookmarkCount,
    cafeName = cafeName
)

