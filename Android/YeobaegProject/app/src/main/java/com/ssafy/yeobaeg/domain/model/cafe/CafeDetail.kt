package com.ssafy.yeobaeg.domain.model.cafe

import com.ssafy.yeobaeg.domain.model.DisplaySlot
import com.ssafy.yeobaeg.domain.model.cafeowner.CafeOwner

/**
 * 카페 상세 정보
 */
data class CafeDetail(
    val cafeId: Long,
    val cafeName: String,
    val address: String,
    val description: String,
    val contact: String,
    val businessHoursStart: String,
    val businessHoursEnd: String,
    val dailyVisitors: String,
    val viewCount: Int,
    val isBookmarked: Boolean,
    val categories: List<String>,
    val images: List<String>,
    val displaySlots: List<DisplaySlot>,
    val cafeOwner: CafeOwner
)

