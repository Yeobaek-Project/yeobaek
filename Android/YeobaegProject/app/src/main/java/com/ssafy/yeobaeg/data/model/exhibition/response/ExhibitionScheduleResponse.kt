package com.ssafy.yeobaeg.data.model.exhibition

import com.ssafy.yeobaeg.domain.model.exhibition.ArtworkSummary
import com.ssafy.yeobaeg.domain.model.exhibition.ExhibitionSchedule
import com.ssafy.yeobaeg.domain.model.exhibition.ExhibitionState
import kotlinx.serialization.Serializable

@Serializable
data class ExhibitionScheduleDto(
    val exhibitionId: Long,
    val artwork: ArtworkSummaryDto,
    val startDate: String,
    val endDate: String,
    val exhibitionState: String
)

@Serializable
data class ArtworkSummaryDto(
    val artworkId: Long,
    val title: String,
    val description: String,
    val artCategory: String
)

fun ExhibitionScheduleDto.toDomain(): ExhibitionSchedule {
    return ExhibitionSchedule(
        exhibitionId = exhibitionId,
        artwork = artwork.toDomain(),
        startDate = startDate,
        endDate = endDate,
        exhibitionState = when (exhibitionState) {
            "ONGOING" -> ExhibitionState.ONGOING
            "SCHEDULED" -> ExhibitionState.SCHEDULED
            "COMPLETED" -> ExhibitionState.COMPLETED
            else -> ExhibitionState.SCHEDULED
        }
    )
}

fun ArtworkSummaryDto.toDomain(): ArtworkSummary {
    return ArtworkSummary(
        artworkId = artworkId,
        title = title,
        description = description,
        artCategory = artCategory
    )
}
