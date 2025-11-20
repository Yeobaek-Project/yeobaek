package com.ssafy.yeobaeg.presentation.arcore.common

import android.annotation.SuppressLint
import com.google.ar.core.HitResult
import dev.romainguy.kotlin.math.Float3
import io.github.sceneview.loaders.MaterialLoader
import io.github.sceneview.math.Color
import io.github.sceneview.math.Position
import io.github.sceneview.node.CubeNode
import io.github.sceneview.node.CylinderNode
import kotlin.math.atan2
import kotlin.math.sqrt

private const val BASE_LINE_HEIGHT = 1.0f
private const val EPS = 1e-4f

/** HitResult -> 월드 좌표 */
fun HitResult.posePosition(): Position =
    Position(hitPose.tx(), hitPose.ty(), hitPose.tz())

/** 3D 거리 */
fun distance(a: Position, b: Position): Float {
    val dx = a.x - b.x
    val dy = a.y - b.y
    val dz = a.z - b.z
    return sqrt(dx * dx + dy * dy + dz * dz)
}

/** 바닥(수평) 두 점을 잇는 실린더 배치 */
fun updateCylinderBetweenGround(line: CylinderNode, a: Position, b: Position) {
    val dx = b.x - a.x
    val dz = b.z - a.z
    val lenXZ = sqrt(dx * dx + dz * dz)
    if (!lenXZ.isFinite() || lenXZ < EPS) { line.isVisible = false; return }
    val floorY = (a.y + b.y) * 0.5f
    val mid = Position((a.x + b.x) * 0.5f, floorY, (a.z + b.z) * 0.5f)
    line.position = mid
    line.rotation = eulerYawOnGround(dx, dz)
    line.scale = Float3(1f, lenXZ / BASE_LINE_HEIGHT, 1f)
    line.isVisible = true
}

/** 임의의 3D 두 점을 잇는 실린더 배치 */
fun updateCylinderBetween3D(line: CylinderNode, a: Position, b: Position) {
    val dx = b.x - a.x
    val dy = b.y - a.y
    val dz = b.z - a.z
    val len = sqrt(dx * dx + dy * dy + dz * dz)
    if (!len.isFinite() || len < EPS) { line.isVisible = false; return }
    val mid = Position((a.x + b.x) * 0.5f, (a.y + b.y) * 0.5f, (a.z + b.z) * 0.5f)
    line.position = mid
    val lenXZ = sqrt(dx * dx + dz * dz).coerceAtLeast(1e-6f)
    val yawRad = atan2(dx, dz)
    val pitchRad = -atan2(lenXZ, dy)
    val rad2deg = 180f / Math.PI.toFloat()
    line.rotation = Float3(pitchRad * rad2deg, yawRad * rad2deg, 0f)
    line.scale = Float3(1f, len / BASE_LINE_HEIGHT, 1f)
    line.isVisible = true
}

/** 수평면에서 yaw (Filament용 XYZ 오일러) */
fun eulerYawOnGround(dx: Float, dz: Float): Float3 {
    val lenXZ = sqrt(dx * dx + dz * dz).coerceAtLeast(1e-6f)
    val yawRad = atan2(dx / lenXZ, dz / lenXZ)
    val rad2deg = 180f / Math.PI.toFloat()
    return Float3(90f, yawRad * rad2deg, 0f)
}

/** 바닥(수평) 두 점을 잇는 큐브 생성 - 입체감 있는 선 */
fun createCubeBetweenGround(
    materialLoader: MaterialLoader,
    a: Position,
    b: Position
): CubeNode {
    val dx = b.x - a.x
    val dz = b.z - a.z
    val lenXZ = sqrt(dx * dx + dz * dz).coerceAtLeast(EPS)
    val floorY = (a.y + b.y) * 0.5f
    val mid = Position((a.x + b.x) * 0.5f, floorY, (a.z + b.z) * 0.5f)

    val boxWidth = 0.01f   // 1cm 폭 (X축)
    val boxLength = lenXZ  // 두 점 사이의 거리 (Y축 - Cylinder처럼)
    val boxDepth = 0.25f   // 5cm 깊이 (Z축) - 입체감 강화

    // Cylinder와 동일하게 Y축을 길이로 사용
    return CubeNode(
        engine = materialLoader.engine,
        size = Float3(boxWidth, boxLength, boxDepth),
        materialInstance = materialLoader.createColorInstance(
            color = Color(0.3f, 0.8f, 0.7f, 0.8f) // 민트색 (투명도 40%)
        )
    ).apply {
        isShadowCaster = false
        position = mid
        // Cylinder와 동일한 회전 로직 (Y축이 길이 방향이므로 X축으로 90도 회전 후 yaw 적용)
        rotation = eulerYawOnGround(dx, dz)
    }
}

@SuppressLint("DefaultLocale")
fun fmt(m: Float) = "${(m).toInt()} cm"