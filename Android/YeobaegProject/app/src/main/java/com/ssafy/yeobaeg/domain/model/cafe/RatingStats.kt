package com.ssafy.yeobaeg.domain.model.cafe

/**
 * 평점 통계 정보
 * - 카페 사장, 작가 등 다양한 엔티티에서 재사용 가능
 */
data class RatingStats(
    val totalReviews: Int,
    val averageRating: Double
)

