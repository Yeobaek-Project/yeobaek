package com.ssafy.yeobaeg.data.model.auth

import kotlinx.serialization.Serializable

@Serializable
data class LoginResponse(
    val accessToken: String,
    val refreshToken: String,
    val userType: String,
    val userId: Long
)

