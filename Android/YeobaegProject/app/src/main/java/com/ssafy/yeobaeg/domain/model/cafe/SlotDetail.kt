package com.ssafy.yeobaeg.domain.model.cafe

/**
 * 전시 슬롯 상세 정보
 */
data class SlotDetail(
    val slotId: Long,
    val slotName: String,
    val widthCm: Int,
    val heightCm: Int,
    val installationMethod: String,
    val commissionRate: Double,
    val locationDescription: String,
    val status: SlotStatus,
    val currentExhibition: Exhibition?,
    val arScanData: String?
)

enum class SlotStatus {
    AVAILABLE,      // 전시 가능
    EXHIBITING,     // 전시 중
    SCHEDULED       // 예약 됨
}

/**
 * 현재 전시 정보
 */
data class Exhibition(
    val artworkId: Long,
    val artworkTitle: String,
    val artworkImageUrl: String,
    val artistName: String,
    val exhibitionStartDate: String,
    val exhibitionEndDate: String
)

