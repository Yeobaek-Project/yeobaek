package com.ssafy.yeobaeg.data.model.request

import kotlinx.serialization.Serializable

@Serializable
data class PresignedUrlRequest(
    val folder: String,
    val count: Int
)
