package com.ssafy.yeobaeg.data.model

import kotlinx.serialization.Serializable

/**
 * 모든 API 응답의 기본 클래스
 *
 * @param T 응답 데이터의 타입
 */
@Serializable
data class BaseResponse<T>(
    val code: String,
    val message: String? = null,
    val data: T? = null,
)
