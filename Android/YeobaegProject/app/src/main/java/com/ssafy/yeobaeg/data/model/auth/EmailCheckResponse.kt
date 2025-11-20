package com.ssafy.yeobaeg.data.model.auth

import kotlinx.serialization.Serializable

/**
 * 이메일 중복 확인 응답
 */
@Serializable
data class EmailCheckResponse(
    val email: String,
    val available: Boolean
)
