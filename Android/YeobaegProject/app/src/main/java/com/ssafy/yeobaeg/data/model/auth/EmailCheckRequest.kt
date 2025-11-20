package com.ssafy.yeobaeg.data.model.auth

import kotlinx.serialization.Serializable

/**
 * 이메일 중복 확인 요청
 */
@Serializable
data class EmailCheckRequest(
    val email: String
)
