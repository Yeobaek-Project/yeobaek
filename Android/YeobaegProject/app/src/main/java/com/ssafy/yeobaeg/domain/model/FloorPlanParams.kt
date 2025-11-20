package com.ssafy.yeobaeg.domain.model

import com.ssafy.yeobaeg.presentation.arcore.common.fmt
import kotlinx.serialization.Serializable

enum class SlotStatus {
    AVAILABLE, UNAVAILABLE, EXHIBITING, RESERVED;

    companion object {
        fun fromString(value: String): SlotStatus {
            return when (value.uppercase()) {
                "AVAILABLE" -> AVAILABLE
                "UNAVAILABLE" -> UNAVAILABLE
                "EXHIBITING" -> EXHIBITING

                else -> AVAILABLE // 기본값
            }
        }
    }
}

@Serializable
data class Vec2(val x: Float, val y: Float) {
    operator fun plus(o: Vec2) = Vec2(x + o.x, y + o.y)
    operator fun minus(o: Vec2) = Vec2(x - o.x, y - o.y)
    operator fun times(s: Float) = Vec2(x * s, y * s)
}


// 도면 생성 응답 dto
data class CreateDisplayWallResponse(
    val wid: Long,
    val wName: String,         // "벽 A", "벽 B" ...
    val start: Vec2,          // 로컬 XZ 시작점
    val end: Vec2,            // 로컬 XZ 끝점
    val status: SlotStatus      // 전시 가능 여부 ("POSSIBLE", "NOT_POSSIBLE", "IN_USE", "RESERVED")
)

// 전시 슬롯 조회 응답 dto
@Serializable
data class DisplaySlot(
    val wid: Long = 0L,
    val wName: String = "",
    val start: Vec2 = Vec2(0f,0f),
    val end: Vec2 = Vec2(0f,0f),
    val width: Float = 0f,
    val height: Float = 0f,
    val status: SlotStatus = SlotStatus.AVAILABLE,
    val commission: Float = 0f, // 수수료
    val requirements: String = "", // 기타 요구 사항
    val exhibition: Exhibition? = null
) {
    val sizeText = "${width.toInt()}cm x ${height.toInt()}cm"
    val widthText get() = fmt(width)
    val heightText get() = fmt(height)
}

@Serializable
data class Exhibition(
    val startDate: String = "",
    val endDate: String = "", // 전시 마감 일자
    val artId: Long = 0L,
    val artTitle: String = "",
    val artist: String = "",
    val artImgUrl: String = ""
)

// 전시 슬롯 생성 요청 dto
data class CreateDisplaySlotRequest(
    val wid: Long,
    val width: Float,
    val height: Float,
    val commission: Float, // 수수료
    val requirements: String, // 기타 요구 사항
)
