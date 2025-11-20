package com.ssafy.yeobaeg.data.model.cafe

import com.ssafy.yeobaeg.domain.model.cafe.Exhibition
import com.ssafy.yeobaeg.domain.model.cafe.SlotDetail
import com.ssafy.yeobaeg.domain.model.cafe.SlotStatus
import kotlinx.serialization.Serializable

@Serializable
data class SlotDetailDto(
    val slotId: Long,
    val slotName: String,
    val widthCm: Int,
    val heightCm: Int,
    val installationMethod: String,
    val commissionRate: Double,
    val locationDescription: String,
    val status: String,
    val currentExhibition: ExhibitionDto?,
    val arScanData: String?
)

@Serializable
data class ExhibitionDto(
    val artworkId: Long,
    val artworkTitle: String,
    val artworkImageUrl: String,
    val artistName: String,
    val exhibitionStartDate: String,
    val exhibitionEndDate: String
)

fun SlotDetailDto.toDomain(): SlotDetail {
    return SlotDetail(
        slotId = slotId,
        slotName = slotName,
        widthCm = widthCm,
        heightCm = heightCm,
        installationMethod = installationMethod,
        commissionRate = commissionRate,
        locationDescription = locationDescription,
        status = when (status) {
            "AVAILABLE" -> SlotStatus.AVAILABLE
            "EXHIBITING" -> SlotStatus.EXHIBITING
            "SCHEDULED" -> SlotStatus.SCHEDULED
            else -> SlotStatus.AVAILABLE
        },
        currentExhibition = currentExhibition?.toDomain(),
        arScanData = arScanData
    )
}

fun ExhibitionDto.toDomain(): Exhibition {
    return Exhibition(
        artworkId = artworkId,
        artworkTitle = artworkTitle,
        artworkImageUrl = artworkImageUrl,
        artistName = artistName,
        exhibitionStartDate = exhibitionStartDate,
        exhibitionEndDate = exhibitionEndDate
    )
}

