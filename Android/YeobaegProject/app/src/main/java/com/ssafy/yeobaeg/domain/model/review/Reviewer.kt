package com.ssafy.yeobaeg.domain.model.review

import com.ssafy.yeobaeg.domain.model.UserRole

data class Reviewer(
    val name: String,
    val profileImageUrl: String?,
    val role: UserRole
)
