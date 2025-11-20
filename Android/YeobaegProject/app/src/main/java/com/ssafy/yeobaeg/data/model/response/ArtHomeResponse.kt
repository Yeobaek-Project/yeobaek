package com.ssafy.yeobaeg.data.model.response

import com.ssafy.yeobaeg.domain.model.artist.ArtHome
import com.ssafy.yeobaeg.domain.model.artist.ArtistHome
import com.ssafy.yeobaeg.domain.model.artist.ArtworkHome
import kotlinx.serialization.Serializable

@Serializable
data class ArtHomeResponse(
    val artist: ArtistHomeResponse,
    val artworks: List<ArtworkHomeResponse>
)

@Serializable
data class ArtistHomeResponse(
    val artistName: String,
    val artworkCount: Int,
    val currentExhibitionCount: Int,
    val profileImageUrl: String? = null,
    val userId: Int
)

@Serializable
data class ArtworkHomeResponse(
    val artworkId: Int,
    val bookmarkCount: Int,
    val cafeName: String? = null,
    val category: String,
    val isExhibiting: Boolean,
    val price: Int,
    val thumbnailUrl: String,
    val title: String
)



fun ArtHomeResponse.toDomain() = ArtHome(
    artist = artist.toDomain(),
    artworks = artworks.map { it.toDomain() }
)

fun ArtistHomeResponse.toDomain() = ArtistHome(
    artistName = artistName,
    artworkCount = artworkCount,
    currentExhibitionCount = currentExhibitionCount,
    profileImageUrl = profileImageUrl ?: "",
    userId = userId
)

fun ArtworkHomeResponse.toDomain() = ArtworkHome(
    artworkId = artworkId,
    bookmarkCount = bookmarkCount,
    cafeName = cafeName,
    category = when (category) {
        "LANDSCAPE"  -> "풍경화"
        "PORTRAIT"   -> "인물화"
        "STILL_LIFE" -> "정물화"
        "HISTORY" -> "역사화"
        "GENRE"      -> "장르화"
        else     -> "기타"
    },
    isExhibiting = isExhibiting,
    price = price,
    thumbnailUrl = thumbnailUrl,
    title = title
)