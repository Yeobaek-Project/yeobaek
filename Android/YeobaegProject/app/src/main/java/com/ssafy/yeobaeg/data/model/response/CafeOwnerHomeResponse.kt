package com.ssafy.yeobaeg.data.model.response

import com.ssafy.yeobaeg.data.model.request.Point
import com.ssafy.yeobaeg.domain.model.DisplaySlot
import com.ssafy.yeobaeg.domain.model.Exhibition
import com.ssafy.yeobaeg.domain.model.SlotStatus
import com.ssafy.yeobaeg.domain.model.Vec2
import com.ssafy.yeobaeg.domain.model.cafeowner.CafeOwnerInfo
import kotlinx.serialization.Serializable

@Serializable
data class CafeOwnerHomeResponse(
    val myCafe: CafeHomeSummaryResponse
)

@Serializable
data class CafeHomeSummaryResponse(
    val address: String,
    val cafeId: Int,
    val cafeName: String,
    val collaboratingArtists: Int,
    val displaySlots: List<DisplaySlotResponse>? = null,
    val exhibitingSlots: Int,
    val primaryImageUrl: String,
    val totalSlots: Int
)

@Serializable
data class DisplaySlotResponse(
    val wallId: Long,
    val wallName: String,
    val width: Float,
    val height: Float,
    val isDisplayable: Boolean,
    val isExhibiting: Boolean = false,
    val start: Point = Point(0.0, 0.0),
    val end: Point = Point(0.0, 0.0),
    val commissionRate: Float = 0f, // 수수료
    val requirements: String = "", // 기타 요구 사항
    val currentExhibition: ExhibitionResponse? = null
)

@Serializable
data class ExhibitionResponse(
    val artworkId: Long,
    val artworkTitle: String,
    val artworkImageUrl: String,
    val artistName: String,
    val exhibitionStartDate: String,
    val exhibitionEndDate: String
)

fun CafeHomeSummaryResponse.toDomain(): CafeOwnerInfo =
    CafeOwnerInfo(
        cafeId = cafeId.toLong(),
        cafeName = cafeName,
        cafeImageUrl = primaryImageUrl,
        cafeAddress = address,
        numExhibitionSpaces = totalSlots,
        numExhibitedWorks = exhibitingSlots,
        numArtistsWorkedWith = collaboratingArtists,
        displaySlots = displaySlots?.map { it.toDomain() } ?: emptyList()
    )

fun DisplaySlotResponse.toDomain() = DisplaySlot(
    wid = wallId,
    wName = wallName,
    width = width,
    height = height,
    start = Vec2(start.x.toFloat(), start.y.toFloat()),
    end = Vec2(end.x.toFloat(), end.y.toFloat()),
    status = if (isExhibiting) SlotStatus.EXHIBITING else if (!isDisplayable) SlotStatus.UNAVAILABLE else SlotStatus.AVAILABLE,
    commission = commissionRate,
    requirements = requirements,
    exhibition = currentExhibition?.toDomain()
)

fun ExhibitionResponse.toDomain(): Exhibition =
    Exhibition(
        startDate = exhibitionStartDate,
        endDate = exhibitionEndDate,
        artId = artworkId,
        artTitle = artworkTitle,
        artist = artistName,
        artImgUrl = artworkImageUrl
    )