package com.ssafy.yeobaeg.data.model.cafe

import com.ssafy.yeobaeg.domain.model.cafeowner.CafeOwner
import kotlinx.serialization.Serializable

@Serializable
data class CafeOwnerDto(
    val userId: Long,
    val nickname: String,
    val profileImageUrl: String?,
    val ratingStats: RatingStatsDto
)

fun CafeOwnerDto.toDomain(): CafeOwner {
    return CafeOwner(
        userId = userId,
        nickname = nickname,
        profileImageUrl = profileImageUrl,
        ratingStats = ratingStats.toDomain()
    )
}

