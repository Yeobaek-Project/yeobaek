package com.ssafy.yeobaeg.data.model.auth

import kotlinx.serialization.Serializable

@Serializable
data class SignUpRequest(
    val userType: String,
    val email: String,
    val password: String,
    val passwordConfirm: String,
    val nickname: String,
    val birthDate: String,
    val gender: String
)

