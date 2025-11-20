package com.ssafy.yeobaeg.data.model.profile

import kotlinx.serialization.Serializable

/**
 * 프로필 평점 요약 정보 응답
 */
@Serializable
data class ProfileRatingSummaryResponse(
    val totalReviews: Int,
    val averageRating: Double
)