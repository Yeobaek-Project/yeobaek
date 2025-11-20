package com.ssafy.yeobaeg.domain.model.cafe

enum class ArtworkSize(
    val label: String,
    val minWidth: Int?,
    val maxWidth: Int?,
    val minHeight: Int?,
    val maxHeight: Int?
) {
    SMALL("소형", null, 50, null, 50),
    MEDIUM("중형", 50, 100, 50, 100),
    LARGE("대형", 100, null, 100, null)
}

