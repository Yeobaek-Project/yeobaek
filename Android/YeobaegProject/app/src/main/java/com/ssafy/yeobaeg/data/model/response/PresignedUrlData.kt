package com.ssafy.yeobaeg.data.model.response

import com.ssafy.yeobaeg.domain.model.storage.PresignedUrl
import kotlinx.serialization.Serializable

@Serializable
data class PresignedUrlResponse(
    val presignedUrls: List<PresignedUrlData>
)

@Serializable
data class PresignedUrlData(
    val uploadUrl: String,
    val downloadUrl: String,
    val fileKey: String
)

fun PresignedUrlData.toDomain() = PresignedUrl(
    uploadUrl = uploadUrl,
    downloadUrl = downloadUrl,
    fileKey = fileKey
)
