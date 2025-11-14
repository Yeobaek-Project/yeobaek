package com.ssafy.yeobaeg.presentation.arcore.plan

import android.annotation.SuppressLint
import android.graphics.Paint
import android.graphics.Typeface
import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import com.ssafy.yeobaeg.domain.model.DisplaySlot
import com.ssafy.yeobaeg.domain.model.SlotStatus
import com.ssafy.yeobaeg.ui.theme.YeobaegGray
import com.ssafy.yeobaeg.ui.theme.YeobaegLightBrown
import kotlin.math.atan2
import kotlin.math.hypot
import kotlin.math.min
import kotlin.math.sqrt

private const val HIT_THRESHOLD_PX = 40f
private const val WALL_HEIGHT_PX = 55f
private const val OUTLINE_STROKE_PX = 4f
private const val CORNER_RADIUS = 8f

// FloorPlanPreview.kt - 조건부 스케일 계산

@SuppressLint("DefaultLocale")
@Composable
fun FloorPlanPreview(
    initialWalls: List<DisplaySlot>,
    modifier: Modifier = Modifier,
    readOnly: Boolean = false,
    showHelpText: Boolean = false,
    onWallTapped: (Long) -> Unit = {},
    activeWallId: Long? = null,
) {
    val wallsState = remember(initialWalls) { initialWalls.toMutableStateList() }
    val walls: List<DisplaySlot> = wallsState

    if (walls.isEmpty()) {
        Box(modifier = modifier) {
            Text("도면 데이터가 없습니다.", modifier = Modifier.align(Alignment.Center))
        }
        return
    }

    val mappedSegmentsState = remember {
        mutableStateOf(emptyList<Triple<Offset, Offset, Long>>())
    }

    Column(modifier = modifier) {
        if(showHelpText) {
            Legend(modifier = Modifier.padding(16.dp))
            Spacer(Modifier.height(8.dp))
        }

        BoxWithConstraints(Modifier.fillMaxWidth()) {
            val density = LocalDensity.current
            val horizontalPad = 32.dp
            val verticalPad = 12.dp
            val horizPx = with(density) { horizontalPad.toPx() }
            val vertPx = with(density) { verticalPad.toPx() }

            val allPoints = walls.flatMap { listOf(it.start, it.end) }
            val minX = allPoints.minOf { it.x }
            val maxX = allPoints.maxOf { it.x }
            val minY = allPoints.minOf { it.y }
            val maxY = allPoints.maxOf { it.y }
            val contentW = (maxX - minX).coerceAtLeast(1e-3f)
            val contentH = (maxY - minY).coerceAtLeast(1e-3f)

            val parentWpx = with(density) { maxWidth.toPx() }
            val parentHpx = with(density) { maxHeight.toPx() }

            // ===== 조건부 스케일 계산 =====
            // 1단계: 너비에 꽉 차게 그릴 때의 스케일
            val scaleByWidth = (parentWpx - 2 * horizPx) / contentW

            // 2단계: 이 스케일로 그렸을 때 실제 도면 높이 계산
            val projectedHeight = contentH * scaleByWidth + 2 * vertPx

            // 3단계: 조건 판단
            val s = if (parentHpx.isInfinite() || projectedHeight <= parentHpx) {
                // 부모 높이가 무한이거나, 도면이 부모 높이보다 작으면 → 너비에 꽉 차게
                scaleByWidth
            } else {
                // 도면이 부모 높이를 넘으면 → 높이에 맞춤
                (parentHpx - 2 * vertPx) / contentH
            }.coerceAtLeast(1e-6f)

            val canvasWpx = parentWpx
            val canvasHpx = parentHpx
            val canvasW = with(density) { canvasWpx.toDp() }
            val canvasH = with(density) { canvasHpx.toDp() }

            // 실제 그려질 도면 크기
            val scaledW = contentW * s
            val scaledH = contentH * s

            // 중앙 정렬 오프셋
            val offsetX = (parentWpx - scaledW) / 2f
            val offsetY = if (parentHpx.isInfinite()) {
                vertPx
            } else {
                (parentHpx - scaledH) / 2f
            }

            val labelPaint = remember {
                Paint().apply {
                    isAntiAlias = true
                    color = "#FFFFFF".toColorInt()
                    textSize = 34f
                    typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                    textAlign = Paint.Align.CENTER
                }
            }
            val sizePaint = remember {
                Paint().apply {
                    isAntiAlias = true
                    color = "#1E293B".toColorInt()
                    textSize = 28f
                    typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
                    textAlign = Paint.Align.CENTER
                }
            }

            fun map(x: Float, y: Float): Offset {
                val mx = (x - minX) * s + offsetX
                val my = (y - minY) * s + offsetY
                return Offset(mx, my)
            }

            Box(
                modifier = Modifier
                    .size(canvasW, canvasH)
                    .align(Alignment.Center)
            ) {
                val canvasModifier = Modifier
                    .matchParentSize()
                    .pointerInput(walls, readOnly) {
                        detectTapGestures(
                            onTap = { tap ->
                                val (wallId, dist) = findNearestSegmentById(
                                    mappedSegmentsState.value,
                                    tap
                                )
                                if (wallId != null && dist <= HIT_THRESHOLD_PX) {
                                    if (!readOnly) {
                                        val idx = walls.indexOfFirst { it.wid == wallId }
                                        if (idx >= 0) toggleWallPossible(wallsState, idx)
                                    }
                                    onWallTapped(wallId)
                                }
                            }
                        )
                    }

                Canvas(modifier = canvasModifier) {
                    val orderedVertices = walls.map { it.start } + listOf(walls.last().end)
                    val mappedVertices = orderedVertices.map { map(it.x, it.y) }

                    if (mappedVertices.size >= 3) {
                        val outline = Path().apply {
                            val p0 = mappedVertices.first()
                            moveTo(p0.x, p0.y)
                            for (i in 1 until mappedVertices.size) {
                                val pi = mappedVertices[i]
                                lineTo(pi.x, pi.y)
                            }
                            close()
                        }
                        drawPath(
                            path = outline,
                            color = YeobaegLightBrown.copy(alpha = 0.1f)
                        )

                        drawPath(
                            path = outline,
                            color = YeobaegGray,
                            style = Stroke(
                                width = OUTLINE_STROKE_PX,
                                pathEffect = PathEffect.dashPathEffect(floatArrayOf(20f, 15f))
                            )
                        )
                    }

                    val mappedSegments = ArrayList<Triple<Offset, Offset, Long>>(walls.size)

                    fun drawWall(wall: DisplaySlot, emphasize: Boolean) {
                        val a = map(wall.start.x, wall.start.y)
                        val b = map(wall.end.x, wall.end.y)

                        val shrinkFactor = 0.1f
                        val dx = b.x - a.x
                        val dy = b.y - a.y
                        val shrunkA = Offset(
                            a.x + dx * shrinkFactor,
                            a.y + dy * shrinkFactor
                        )
                        val shrunkB = Offset(
                            b.x - dx * shrinkFactor,
                            b.y - dy * shrinkFactor
                        )

                        val wallLength = hypot(shrunkB.x - shrunkA.x, shrunkB.y - shrunkA.y)
                        val midX = (shrunkA.x + shrunkB.x) / 2f
                        val midY = (shrunkA.y + shrunkB.y) / 2f

                        var angle = Math.toDegrees(
                            atan2(
                                (shrunkB.y - shrunkA.y).toDouble(),
                                (shrunkB.x - shrunkA.x).toDouble()
                            )
                        ).toFloat()

                        var textAngle = angle
                        if (angle > 90f || angle < -90f) {
                            textAngle = angle + 180f
                        }

                        val baseColor = wallColor(wall.status)

                        val rectPath = Path().apply {
                            addRoundRect(
                                androidx.compose.ui.geometry.RoundRect(
                                    left = midX - wallLength / 2f,
                                    top = midY - WALL_HEIGHT_PX / 2f,
                                    right = midX + wallLength / 2f,
                                    bottom = midY + WALL_HEIGHT_PX / 2f,
                                    radiusX = CORNER_RADIUS,
                                    radiusY = CORNER_RADIUS
                                )
                            )
                        }

                        mappedSegments.add(Triple(shrunkA, shrunkB, wall.wid))

                        rotate(angle, Offset(midX, midY)) {
                            if (emphasize) {
                                val expand1 = 8f
                                val highlightRect1 = Path().apply {
                                    addRoundRect(
                                        androidx.compose.ui.geometry.RoundRect(
                                            left = midX - wallLength / 2f - expand1,
                                            top = midY - WALL_HEIGHT_PX / 2f - expand1,
                                            right = midX + wallLength / 2f + expand1,
                                            bottom = midY + WALL_HEIGHT_PX / 2f + expand1,
                                            radiusX = CORNER_RADIUS + expand1,
                                            radiusY = CORNER_RADIUS + expand1
                                        )
                                    )
                                }
                                val expand2 = 10f
                                val highlightRect2 = Path().apply {
                                    addRoundRect(
                                        androidx.compose.ui.geometry.RoundRect(
                                            left = midX - wallLength / 2f - expand2,
                                            top = midY - WALL_HEIGHT_PX / 2f - expand2,
                                            right = midX + wallLength / 2f + expand2,
                                            bottom = midY + WALL_HEIGHT_PX / 2f + expand2,
                                            radiusX = CORNER_RADIUS + expand2,
                                            radiusY = CORNER_RADIUS + expand2
                                        )
                                    )
                                }
                                drawPath(
                                    path = highlightRect1,
                                    color = Color(0xFF22D3EE).copy(alpha = 0.3f)
                                )
                                drawPath(
                                    path = highlightRect2,
                                    color = Color(0xFF22D3EE).copy(alpha = 0.6f)
                                )
                                val glowAlphas = listOf(0.10f, 0.06f)
                                val glowWidths = listOf(18f, 34f)
                                for (i in glowAlphas.indices) {
                                    drawPath(
                                        path = rectPath,
                                        color = Color(0xFFFBBF24).copy(alpha = glowAlphas[i]),
                                        style = Stroke(width = glowWidths[i])
                                    )
                                }
                            }

                            drawPath(rectPath, baseColor)
                        }

                        val name = wall.wName.ifBlank { "벽 ${wall.wid}" }
                        drawContext.canvas.nativeCanvas.save()
                        drawContext.canvas.nativeCanvas.apply {
                            rotate(textAngle, midX, midY)
                            val fm = labelPaint.fontMetrics
                            val textY = midY - (fm.ascent + fm.descent) / 2f
                            drawText(name, midX, textY, labelPaint)
                        }
                        drawContext.canvas.nativeCanvas.restore()

                        val dx2 = wall.end.x - wall.start.x
                        val dy2 = wall.end.y - wall.start.y
                        val len = sqrt(dx2 * dx2 + dy2 * dy2).coerceAtLeast(1e-6f)

                        val ccw = isCCW(mappedVertices)
                        var nx = dy2 / len
                        var ny = -dx2 / len
                        if (!ccw) {
                            nx = -nx
                            ny = -ny
                        }

                        val sizeOffset = WALL_HEIGHT_PX / 2f + 35f
                        val sizePosX = midX + nx * sizeOffset
                        val sizePosY = midY + ny * sizeOffset

                        val widthText = wall.widthText
                        val sizeText = if (wall.height <= 0f) {
                            widthText
                        } else {
                            "${widthText} × ${wall.heightText}"
                        }

                        drawContext.canvas.nativeCanvas.save()
                        drawContext.canvas.nativeCanvas.apply {
                            rotate(textAngle, sizePosX, sizePosY)
                            val sfm = sizePaint.fontMetrics
                            val sizeTextY = sizePosY - (sfm.ascent + sfm.descent) / 2f
                            drawText(sizeText, sizePosX, sizeTextY, sizePaint)
                        }
                        drawContext.canvas.nativeCanvas.restore()
                    }

                    walls.filter { it.wid != activeWallId }.forEach { drawWall(it, emphasize = false) }
                    walls.firstOrNull { it.wid == activeWallId }?.let { drawWall(it, emphasize = true) }

                    mappedSegmentsState.value = mappedSegments
                }
            }
        }
    }
}

// Helper functions remain the same
private fun findNearestSegmentById(
    segments: List<Triple<Offset, Offset, Long>>,
    tap: Offset
): Pair<Long?, Float> {
    var bestWallId: Long? = null
    var bestDist = Float.POSITIVE_INFINITY
    segments.forEach { (a, b, wallId) ->
        val d = pointToSegmentDistance(tap, a, b)
        if (d < bestDist) {
            bestDist = d
            bestWallId = wallId
        }
    }
    return bestWallId to bestDist
}

private fun pointToSegmentDistance(p: Offset, a: Offset, b: Offset): Float {
    val abx = b.x - a.x
    val aby = b.y - a.y
    val apx = p.x - a.x
    val apy = p.y - a.y
    val ab2 = abx * abx + aby * aby
    if (ab2 <= 1e-6f) return hypot(apx, apy)
    val t = ((apx * abx) + (apy * aby)) / ab2
    val tt = t.coerceIn(0f, 1f)
    val cx = a.x + abx * tt
    val cy = a.y + aby * tt
    return hypot(p.x - cx, p.y - cy)
}

private fun wallColor(status: SlotStatus): Color {
    return when (status) {
        SlotStatus.UNAVAILABLE -> Color(0xFFCBD5E1)
        SlotStatus.AVAILABLE -> Color(0xFF10B981)
        SlotStatus.EXHIBITING -> Color(0xFFFFC107)
        SlotStatus.RESERVED -> Color(0xFF684352)
    }
}

private fun isCCW(vertices: List<Offset>): Boolean {
    if (vertices.size < 3) return true
    var area2 = 0f
    for (i in 0 until vertices.size - 1) {
        val p = vertices[i]
        val q = vertices[i + 1]
        area2 += p.x * q.y - q.x * p.y
    }
    return area2 > 0f
}

private fun toggleWallPossible(walls: MutableList<DisplaySlot>, index: Int) {
    if (index !in walls.indices) return
    val w = walls[index]
    val newStatus = when (w.status) {
        SlotStatus.UNAVAILABLE -> SlotStatus.AVAILABLE
        SlotStatus.AVAILABLE, SlotStatus.EXHIBITING, SlotStatus.RESERVED -> SlotStatus.UNAVAILABLE
    }
    walls[index] = w.copy(status = newStatus)
}

@Composable
private fun Legend(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        LegendItem(color = Color(0xFF10B981), label = "선택 가능")
        LegendItem(color = Color(0xFFCBD5E1), label = "전시 불가능")
//        LegendItem(color = Color(0xFFF59E0B), label = "", isHighlight = true)
    }
}

@Composable
private fun LegendItem(
    color: Color,
    label: String,
    isHighlight: Boolean = false
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = if (isHighlight) {
            Modifier
                .background(color.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                .padding(horizontal = 12.dp, vertical = 6.dp)
        } else Modifier
    ) {
        if (isHighlight) {
            Text("×", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF64748B))
        } else {
            Box(Modifier.size(24.dp, 16.dp).background(color, RoundedCornerShape(4.dp)))
        }
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = if (isHighlight) FontWeight.SemiBold else FontWeight.Normal,
            color = Color(0xFF1E293B)
        )
    }
}
