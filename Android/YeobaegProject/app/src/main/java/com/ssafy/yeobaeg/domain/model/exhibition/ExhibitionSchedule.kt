package com.ssafy.yeobaeg.domain.model.exhibition

/**
 * 전시 일정 정보
 */
data class ExhibitionSchedule(
    val exhibitionId: Long,
    val artwork: ArtworkSummary,
    val startDate: String,
    val endDate: String,
    val exhibitionState: ExhibitionState
)

/**
 * 작품 요약 정보
 */
data class ArtworkSummary(
    val artworkId: Long,
    val title: String,
    val description: String,
    val artCategory: String
)

/**
 * 전시 상태
 */
enum class ExhibitionState {
    SCHEDULED,  // 예정
    ONGOING,    // 진행 중
    COMPLETED   // 완료
}
