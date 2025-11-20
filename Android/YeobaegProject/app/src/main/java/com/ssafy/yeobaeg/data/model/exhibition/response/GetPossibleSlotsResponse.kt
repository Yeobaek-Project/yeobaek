package com.ssafy.yeobaeg.data.model.exhibition.response

import com.ssafy.yeobaeg.domain.model.exhibition.PossibleArtworks
import com.ssafy.yeobaeg.domain.model.exhibition.PossibleSlots
import kotlinx.serialization.Serializable

@Serializable
data class GetPossibleSlotsResponse(
    val slots: List<PossibleSlotSummaryResponse>
)

fun GetPossibleSlotsResponse.toDomain(): PossibleSlots {
    return PossibleSlots(
        slots = slots.toDomain()
    )
}