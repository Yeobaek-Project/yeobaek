package com.ssafy.yeobaeg.data.model.review

import com.ssafy.yeobaeg.domain.model.UserRole
import com.ssafy.yeobaeg.domain.model.review.Reviewer
import kotlinx.serialization.Serializable

@Serializable
data class ReviewerDto(
    val name: String,
    val profileImageUrl: String? = null,
    val role: UserRole
)

fun ReviewerDto.toDomain(): Reviewer = Reviewer(
    name = name,
    profileImageUrl = profileImageUrl,
    role = role
)
