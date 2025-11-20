package com.ssafy.yeobaeg.data.model.review

import com.ssafy.yeobaeg.domain.model.review.ReviewDetail
import kotlinx.serialization.Serializable

@Serializable
data class ReviewDetailDto(
    val reviewId: Long,
    val reviewer: ReviewerDto,
    val rating: Int,
    val comment: String,
    val createdAt: String
)

fun ReviewDetailDto.toDomain(): ReviewDetail = ReviewDetail(
    reviewId = reviewId,
    reviewer = reviewer.toDomain(),
    rating = rating,
    comment = comment,
    exhibitionStartDate = null,
    exhibitionEndDate = null,
    createdAt = createdAt
)
