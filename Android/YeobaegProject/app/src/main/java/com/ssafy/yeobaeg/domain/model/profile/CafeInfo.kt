package com.ssafy.yeobaeg.domain.model.profile

data class CafeInfo(
    val cafeId: Long=0L,
    val cafeName: String="",
    val address: String="",
    val description: String="",
    val contact: String="",
    val businessHoursStart: String="",
    val businessHoursEnd:String="",
    val ratingStats: ProfileRatingSummary = ProfileRatingSummary()
)