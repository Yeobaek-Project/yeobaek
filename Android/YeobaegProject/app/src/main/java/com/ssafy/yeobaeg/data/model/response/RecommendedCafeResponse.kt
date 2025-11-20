package com.ssafy.yeobaeg.data.model.response

import com.ssafy.yeobaeg.data.model.PaginationResponse
import com.ssafy.yeobaeg.data.model.toDomain
import com.ssafy.yeobaeg.domain.model.cafe.CafeSlot
import com.ssafy.yeobaeg.domain.model.cafe.RecommendedCafe
import com.ssafy.yeobaeg.domain.model.cafe.RecommendedCafes
import kotlinx.serialization.Serializable

@Serializable
data class RecommendedCafesResponse(
    val cafes: List<RecommendedCafeResponse>,
)

@Serializable
data class RecommendedCafeResponse(
    val address: String,
    val cafeId: Long,
    val cafeName: String,
    val categories: List<String>,
    val primaryImageUrl: String,
    val slots: List<Slot>? = emptyList(),
    val totalReviewScore: Double
)

@Serializable
data class Slot(
    val isExhibiting: Boolean,
    val wallName: String
)

fun Slot.toDomain(): CafeSlot {
    return CafeSlot(
        wallName = wallName,
        isExhibiting = isExhibiting
    )
}

fun RecommendedCafeResponse.toDomain(): RecommendedCafe {
    return RecommendedCafe(
        cafeId = cafeId,
        cafeName = cafeName,
        address = address,
        primaryImageUrl = primaryImageUrl,
        totalReviewScore = totalReviewScore,
        categories = categories,
        slots = slots
            ?.map { it.toDomain() }
            ?.sortedBy { it.isExhibiting }
            ?: emptyList()
    )
}

fun RecommendedCafesResponse.toDomain(): RecommendedCafes {
    return RecommendedCafes(
        cafes = cafes.map { it.toDomain() },
    )
}