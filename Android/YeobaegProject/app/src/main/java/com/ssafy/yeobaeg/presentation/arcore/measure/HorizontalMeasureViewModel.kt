package com.ssafy.yeobaeg.presentation.arcore.horizontal

import androidx.lifecycle.viewModelScope
import com.google.ar.core.Pose
import com.ssafy.yeobaeg.domain.model.DisplaySlot
import com.ssafy.yeobaeg.domain.model.Vec2
import com.ssafy.yeobaeg.domain.model.SlotStatus
import com.ssafy.yeobaeg.navigation.FullScreen
import com.ssafy.yeobaeg.navigation.navigator.Navigator
import com.ssafy.yeobaeg.presentation.arcore.measure.MeasureBaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.sceneview.math.Position
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.round
import kotlin.math.sin
import kotlin.math.sqrt

@HiltViewModel
class HorizontalMeasureViewModel @Inject constructor(
    private val nav: Navigator
) : MeasureBaseViewModel(nav) {

    fun buildAndNavigate() {
        val s = uiState.value
        setBuilding(true)
        viewModelScope.launch {
            val planePose = s.planePose
            val corners   = s.cornersWorld
            if (planePose != null && corners.size >= 3) {
                val local   = worldToPlaneLocalXZ(planePose, corners)
                val snapped = snapPolylineAngles(local, 45f, 0f)
                val closed  = closeIfNear(snapped)
                val walls   = buildWallsFromPolyline(closed)
                nav.navigate(FullScreen.WallEdit(walls))
            }
            setBuilding(false)
        }
    }

    /* ---- 내부 유틸 (수평 전용) ---- */

    private fun worldToPlaneLocalXZ(plane: Pose, pts: List<Position>): List<Vec2> =
        pts.map { p ->
            val out = plane.inverse().transformPoint(floatArrayOf(p.x, p.y, p.z))
            Vec2(out[0], out[2])
        }

    private fun snapPolylineAngles(poly: List<Vec2>, stepDeg: Float, tolDeg: Float): List<Vec2> {
        if (poly.size < 2) return poly
        val step = Math.toRadians(stepDeg.toDouble())
        val tol  = Math.toRadians(tolDeg.toDouble())
        fun nearest(a: Double): Double {
            val k = round(a / step)
            val target = k * step
            return if (abs(a - target) <= tol) target else a
        }
        val res = ArrayList<Vec2>(poly.size)
        res += poly.first()
        for (i in 1 until poly.size) {
            val prev = res.last()
            val cur  = poly[i]
            val dx = (cur.x - prev.x).toDouble()
            val dy = (cur.y - prev.y).toDouble()
            val len = hypot(dx, dy).coerceAtLeast(1e-6)
            val ang = atan2(dy, dx)
            val sna = nearest(ang)
            val nx = cos(sna); val ny = sin(sna)
            res += Vec2(prev.x + (nx * len).toFloat(), prev.y + (ny * len).toFloat())
        }
        return res
    }

    private fun closeIfNear(poly: List<Vec2>, tol: Float = 0.05f): List<Vec2> {
        if (poly.size < 3) return poly
        val a = poly.first(); val b = poly.last()
        val dx = a.x - b.x;   val dy = a.y - b.y
        return if (sqrt(dx*dx + dy*dy) <= tol) poly.dropLast(1) + a else poly
    }

    private fun buildWallsFromPolyline(poly: List<Vec2>): List<DisplaySlot> {
        if (poly.size < 2) return emptyList()
        val result = ArrayList<DisplaySlot>(poly.size)
        for (i in 0 until poly.size) {
            val a = poly[i]; val b = poly[(i + 1) % poly.size]
            val dx = b.x - a.x; val dy = b.y - a.y
            val len = sqrt(dx * dx + dy * dy) * 100
            if (!len.isFinite() || len <= 1e-6f) continue
            result += DisplaySlot(
                wid = i + 1L,
                start = a,
                end = b,
                height = 0f,
                width = len,
                wName = "벽 ${i + 1}",
                status = SlotStatus.AVAILABLE
            )
        }
        return result
    }
}
