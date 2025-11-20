package com.ssafy.yeobaeg.data.model.review

import com.ssafy.yeobaeg.domain.model.review.ReviewDetail
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WrittenReviewDetailDto(
    val reviewId: Long,
    @SerialName("reviewee")
    val reviewer: ReviewerDto,
    val rating: Int,
    val comment: String,
    val createdAt: String
)

fun WrittenReviewDetailDto.toDomain(): ReviewDetail = ReviewDetail(
    reviewId = reviewId,
    reviewer = reviewer.toDomain(),
    rating = rating,
    comment = comment,
    exhibitionStartDate = null,
    exhibitionEndDate = null,
    createdAt = createdAt
)