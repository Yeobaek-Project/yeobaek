package com.ssafy.yeobaeg.data.model.profile

import com.ssafy.yeobaeg.domain.model.Gender
import kotlinx.serialization.Serializable

/**
 * 일반 프로필 정보
 */
@Serializable
data class UserProfileResponse(
    val userId: Long,
    val email: String="",
    val nickname: String,
    val userType: String,
    val profileImageUrl: String?=null,
    val birthDate: String="",
    val gender: Gender=Gender.MALE
)