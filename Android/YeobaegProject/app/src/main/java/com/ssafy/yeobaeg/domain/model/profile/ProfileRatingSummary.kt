package com.ssafy.yeobaeg.domain.model.profile

/**
 * 프로필에 표시되는 평점 요약 정보
 * (총 리뷰 수, 평균 평점)
 */
data class ProfileRatingSummary(
    val totalReviews: Int = 0,
    val averageRating: Double = 0.0
)