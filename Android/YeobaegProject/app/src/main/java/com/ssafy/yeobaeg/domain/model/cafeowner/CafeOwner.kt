package com.ssafy.yeobaeg.domain.model.cafeowner

import com.ssafy.yeobaeg.domain.model.cafe.RatingStats

data class CafeOwner(
    val userId: Long,
    val nickname: String,
    val profileImageUrl: String?,
    val ratingStats: RatingStats
)