package com.ssafy.yeobaeg.domain.model.review

import com.ssafy.yeobaeg.domain.model.Pagination

data class ReviewList(
    val reviews: List<ReviewDetail>,
    val pagination: Pagination
)
