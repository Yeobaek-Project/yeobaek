package com.ssafy.yeobaeg.data.model.request

import com.ssafy.yeobaeg.domain.model.DisplaySlot
import com.ssafy.yeobaeg.domain.model.Vec2
import com.ssafy.yeobaeg.domain.model.SlotStatus
import kotlinx.serialization.Serializable

@Serializable
data class CreateDisplayWallRequest(
    val wallName: String,         // "벽 A", "벽 B" ...
    val start: Point,          // 로컬 XZ 시작점
    val end: Point,            // 로컬 XZ 끝점
    val status: Boolean,      // 전시 가능 여부 true 이면 POSSIBLE, false 이면 NOT_POSSIBLE
    val width: Int,
    val height: Int
)

@Serializable
data class Point(
    val x: Double,
    val y: Double
)

fun DisplaySlot.toDto(): CreateDisplayWallRequest =
    CreateDisplayWallRequest(
        wallName = wName,
        start = start.toPoint(),
        end = end.toPoint(),
        status = status == SlotStatus.AVAILABLE,   // POSSIBLE -> true, others -> false
        width = width.toInt(),
        height = height.toInt()
    )


private fun Vec2.toPoint(): Point =
    Point(
        x = x.toDouble(),
        y = y.toDouble()
    )
