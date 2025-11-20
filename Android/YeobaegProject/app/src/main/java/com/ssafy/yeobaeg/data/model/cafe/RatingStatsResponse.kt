package com.ssafy.yeobaeg.data.model.cafe

import com.ssafy.yeobaeg.domain.model.cafe.RatingStats
import kotlinx.serialization.Serializable

@Serializable
data class RatingStatsDto(
    val totalReviews: Int,
    val averageRating: Double
)

fun RatingStatsDto.toDomain(): RatingStats {
    return RatingStats(
        totalReviews = totalReviews,
        averageRating = averageRating
    )
}

