package com.ssafy.yeobaeg.data.model.exhibition.request

import com.ssafy.yeobaeg.domain.model.exhibition.ProposeExhibition
import kotlinx.serialization.Serializable

@Serializable
data class ProposeExhibitionRequest(
    val cafeId: Long,
    val artworkId: Long,
    val slotId: Long,
    val startDate: String,
    val endDate: String,
    val commissionRate: Double,
    val requirements: String
)

fun ProposeExhibition.toDto(): ProposeExhibitionRequest =
    ProposeExhibitionRequest(
        cafeId = cafeId,
        artworkId = artworkId,
        slotId = slotId,
        startDate = startDate,
        endDate = endDate,
        commissionRate = commissionRate,
        requirements = requirements
    )