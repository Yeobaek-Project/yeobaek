package com.ssafy.yeobaeg.data.model.review

import com.ssafy.yeobaeg.data.model.PaginationResponse
import com.ssafy.yeobaeg.data.model.toDomain
import com.ssafy.yeobaeg.domain.model.review.ReviewList
import kotlinx.serialization.Serializable

@Serializable
data class ReviewListResponse(
    val reviews: List<ReviewDetailDto>,
    val pagination: PaginationResponse
)

fun ReviewListResponse.toDomain(): ReviewList = ReviewList(
    reviews = reviews.map { it.toDomain() },
    pagination = pagination.toDomain()
)
