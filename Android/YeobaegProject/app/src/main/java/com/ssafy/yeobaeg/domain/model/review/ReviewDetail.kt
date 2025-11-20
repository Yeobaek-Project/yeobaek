package com.ssafy.yeobaeg.domain.model.review

data class ReviewDetail(
    val reviewId: Long,
    val reviewer: Reviewer,
    val rating: Int,
    val comment: String,
    val exhibitionStartDate: String?,
    val exhibitionEndDate: String?,
    val createdAt: String
)
