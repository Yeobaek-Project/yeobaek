package com.ssafy.yeobaeg.data.model.profile

import kotlinx.serialization.Serializable

/**
 * 프로필 아티스트 정보
 */
@Serializable
data class ArtistProfileResponse(
    val introduction: String? = "",
    val category: String? = null,
    val artworkCount: Int,
    val exhibitingCount: Int,
    val ratingStats: ProfileRatingSummaryResponse
)