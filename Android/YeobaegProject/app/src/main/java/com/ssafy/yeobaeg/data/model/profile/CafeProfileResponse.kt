package com.ssafy.yeobaeg.data.model.profile

import kotlinx.serialization.Serializable

/**
 * 프로필 카페 정보
 */
@Serializable
data class CafeProfileResponse(
    val cafeId: Long,
    val cafeName: String,
    val address: String,
    val description: String,
    val contact: String,
    val businessHoursStart:String,
    val businessHoursEnd: String,
    val ratingStats: ProfileRatingSummaryResponse
)