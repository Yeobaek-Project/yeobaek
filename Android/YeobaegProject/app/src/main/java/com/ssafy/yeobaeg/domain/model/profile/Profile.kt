package com.ssafy.yeobaeg.domain.model.profile

import com.ssafy.yeobaeg.domain.model.profile.CafeInfo

data class Profile (
    val userInfo: UserInfo,
    val cafeInfo: CafeInfo?=null,
    val artistInfo: ArtistInfo?=null
)