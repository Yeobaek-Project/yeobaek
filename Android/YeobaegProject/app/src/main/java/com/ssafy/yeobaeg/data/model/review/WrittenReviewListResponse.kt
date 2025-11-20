package com.ssafy.yeobaeg.data.model.review

import com.ssafy.yeobaeg.data.model.PaginationResponse
import com.ssafy.yeobaeg.data.model.toDomain
import com.ssafy.yeobaeg.domain.model.review.ReviewList
import kotlinx.serialization.Serializable

@Serializable
data class WrittenReviewListResponse(
    val reviews: List<WrittenReviewDetailDto>,
    val pagination: PaginationResponse
)

fun WrittenReviewListResponse.toDomain(): ReviewList = ReviewList(
    reviews = reviews.map { it.toDomain() },
    pagination = pagination.toDomain()
)