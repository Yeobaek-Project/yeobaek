package com.ssafy.yeobaeg.data.model.review

import com.ssafy.yeobaeg.domain.model.review.RatingStat
import kotlinx.serialization.Serializable

@Serializable
data class RatingStatDto(
    val rating: Int,
    val count: Int,
    val percentage: Double
)

fun RatingStatDto.toDomain(): RatingStat = RatingStat(
    rating = rating,
    count = count,
    percentage = percentage
)
