package com.ssafy.yeobaeg.data.model.response

import kotlinx.serialization.Serializable

@Serializable
data class ToggleBookmarkResponse(
    val isBookmarked: Boolean
)