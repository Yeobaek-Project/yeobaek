package com.ssafy.yeobaeg.data.model.chat.response

import com.ssafy.yeobaeg.domain.model.chat.ExhibitionInfo
import kotlinx.serialization.Serializable

@Serializable
data class ExhibitionInfoResponse(
    val exhibitionId: Long,
    val cafeName: String,
    val slotName: String,
    val artworkTitle:String,
    val exhibitionStartDate: String,
    val exhibitionEndDate: String,
    val commissionRate: Double,
    val requirements: String,
)

fun ExhibitionInfoResponse.toDomain(): ExhibitionInfo {
    return ExhibitionInfo(
        exhibitionId = exhibitionId,
        cafeName = cafeName,
        slotName = slotName,
        artworkTitle=artworkTitle,
        exhibitionStartDate = exhibitionStartDate,
        exhibitionEndDate = exhibitionEndDate,
        commissionRate = commissionRate,
        requirements = requirements,
    )
}