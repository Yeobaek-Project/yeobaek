package com.ssafy.yeobaeg.domain.model.review

data class ReviewStatistics(
    val totalReviewCount: Int,
    val totalReviewScore: Double,
    val ratingStats: List<RatingStat>
)