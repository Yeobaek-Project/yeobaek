package com.ssafy.yeobaeg.domain.model.profile

data class ArtistInfo(
    val introduction: String="",
    val artworkCount: Int=0,
    val exhibitingCount: Int=0,
    val ratingStats: ProfileRatingSummary= ProfileRatingSummary()
)