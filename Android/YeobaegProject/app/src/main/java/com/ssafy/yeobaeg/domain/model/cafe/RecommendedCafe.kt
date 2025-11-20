package com.ssafy.yeobaeg.domain.model.cafe

data class RecommendedCafe(
    val cafeId: Long,
    val cafeName: String,
    val address: String,
    val primaryImageUrl: String,
    val totalReviewScore: Double,
    val categories: List<String>,
    val slots: List<CafeSlot>
)

data class CafeSlot(
    val wallName: String,
    val isExhibiting: Boolean
)

data class RecommendedCafes(
    val cafes: List<RecommendedCafe>,
    val pagination: com.ssafy.yeobaeg.domain.model.Pagination? = null
)