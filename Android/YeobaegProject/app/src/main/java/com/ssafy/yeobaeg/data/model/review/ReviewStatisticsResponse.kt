package com.ssafy.yeobaeg.data.model.review

import com.ssafy.yeobaeg.domain.model.review.ReviewStatistics
import kotlinx.serialization.Serializable

@Serializable
data class ReviewStatisticsResponse(
    val totalReviewCount: Int,
    val totalReviewScore: Double,
    val ratingStats: List<RatingStatDto>
)

fun ReviewStatisticsResponse.toDomain(): ReviewStatistics = ReviewStatistics(
    totalReviewCount = totalReviewCount,
    totalReviewScore = totalReviewScore,
    ratingStats = ratingStats.map { it.toDomain() }
)
