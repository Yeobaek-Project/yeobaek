package com.ssafy.yeobaeg.domain.model.storage

data class PresignedUrl(
    val uploadUrl: String,
    val downloadUrl: String,
    val fileKey: String
)