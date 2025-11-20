package com.ssafy.yeobaeg.data.model.profile

import kotlinx.serialization.Serializable

@Serializable
data class UpdateProfileRequest(
    val nickname: String? = null,
    val profileImageUrl: String? = null,
    val introduction: String? = null
)