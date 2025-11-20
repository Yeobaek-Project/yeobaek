package com.ssafy.yeobaeg.data.model.review

import kotlinx.serialization.Serializable

@Serializable
data class ReviewStatisticsDto(
    val totalReviewCount: Int,
    val totalReviewScore: Double,
    val ratingStats: List<RatingStatDto>
)
