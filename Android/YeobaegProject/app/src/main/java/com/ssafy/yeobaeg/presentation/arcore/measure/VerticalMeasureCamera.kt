package com.ssafy.yeobaeg.presentation.arcore.measure

import android.view.MotionEvent
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.ar.core.Config
import com.google.ar.core.Frame
import com.google.ar.core.Plane
import com.google.ar.core.Session
import com.google.ar.core.TrackingFailureReason
import com.google.ar.core.TrackingState
import com.ssafy.yeobaeg.presentation.arcore.common.detachAnchorSafe
import com.ssafy.yeobaeg.presentation.arcore.common.distance
import com.ssafy.yeobaeg.presentation.arcore.common.posePosition
import com.ssafy.yeobaeg.presentation.arcore.common.removeNodeSafe
import com.ssafy.yeobaeg.presentation.arcore.common.updateCylinderBetween3D
import com.ssafy.yeobaeg.presentation.arcore.horizontal.CenterReticle
import com.ssafy.yeobaeg.presentation.arcore.horizontal.DistanceHUD
import com.ssafy.yeobaeg.presentation.arcore.horizontal.failReasonToString
import com.ssafy.yeobaeg.presentation.common.component.YeobaegButton
import io.github.sceneview.ar.ARScene
import io.github.sceneview.ar.node.AnchorNode
import io.github.sceneview.ar.rememberARCameraStream
import io.github.sceneview.math.Position
import io.github.sceneview.node.CylinderNode
import io.github.sceneview.node.SphereNode
import io.github.sceneview.rememberEngine
import io.github.sceneview.rememberMaterialLoader
import io.github.sceneview.rememberNodes
import kotlinx.coroutines.android.awaitFrame
import kotlinx.coroutines.launch

private const val BASE_LINE_HEIGHT = 1.0f

@Composable
fun VerticalMeasureCamera(
    modifier: Modifier = Modifier,
    onMeasured: (Float) -> Unit = {},
    onCancel: () -> Unit = {},
    viewModel: VerticalMeasureViewModel = hiltViewModel()
) {
    val engine = rememberEngine()
    val materialLoader = rememberMaterialLoader(engine)
    val cameraStream = rememberARCameraStream(materialLoader)
    val nodes = rememberNodes()

    var viewSize by remember { mutableStateOf(IntSize.Zero) }
    var frame by remember { mutableStateOf<Frame?>(null) }
    var lastPoint by remember { mutableStateOf<Position?>(null) }
    var liveDistance by remember { mutableStateOf<Float?>(null) }
    val segments = remember { mutableStateListOf<Seg>() }
    var centerHasPlane by remember { mutableStateOf(false) }
    var liveLine by remember { mutableStateOf<CylinderNode?>(null) }
    var liveDot by remember { mutableStateOf<SphereNode?>(null) }

    var ready by remember { mutableStateOf(false) }
    var isDisposing by remember { mutableStateOf(false) }
    var sceneKey by remember { mutableIntStateOf(0) }
    val scope = rememberCoroutineScope()

    val maxPoints = 2
    val canAddPoint = segments.size < maxPoints
    var trackingFailureReason by remember { mutableStateOf<TrackingFailureReason?>(null) }

    LaunchedEffect(viewSize) {
        ready = if (viewSize.width > 0 && viewSize.height > 0) {
            awaitFrame(); true
        } else false
    }

    fun clearInScene() {
        liveLine?.let { removeNodeSafe(it, nodes) }
        liveLine = null
        liveDot?.let { removeNodeSafe(it, nodes) }
        liveDot = null
        segments.forEach { seg ->
            seg.line?.let { removeNodeSafe(it, nodes) }
            removeNodeSafe(seg.marker, nodes)
            detachAnchorSafe(seg.anchorNode)
            removeNodeSafe(seg.anchorNode, nodes)
        }
        segments.clear()
        lastPoint = null
        liveDistance = null
    }

    fun disposeWithSceneAlive() {
        if (isDisposing) return
        isDisposing = true
        ready = false
        clearInScene()
    }

    fun disposeOnComposeDisposeOnly() {
        liveLine = null; liveDot = null
        segments.clear(); lastPoint = null; liveDistance = null
        ready = false; isDisposing = true
    }

    DisposableEffect(Unit) { onDispose { disposeOnComposeDisposeOnly() } }
    BackHandler(enabled = true) {
        disposeWithSceneAlive()
        viewModel.onBackPressed()
        onCancel()
    }

    MaterialTheme {
        Box(modifier.onSizeChanged { viewSize = it }) {
            if (ready) {
                key(sceneKey) {
                    ARScene(
                        modifier = Modifier.fillMaxSize(),
                        engine = engine,
                        materialLoader = materialLoader,
                        cameraStream = cameraStream,
                        childNodes = nodes,
                        sessionConfiguration = { s: Session, cfg: Config ->
                            cfg.updateMode = Config.UpdateMode.LATEST_CAMERA_IMAGE
                            cfg.focusMode = Config.FocusMode.AUTO
                            cfg.depthMode = when {
                                s.isDepthModeSupported(Config.DepthMode.AUTOMATIC) ->
                                    Config.DepthMode.AUTOMATIC

                                s.isDepthModeSupported(Config.DepthMode.RAW_DEPTH_ONLY) ->
                                    Config.DepthMode.RAW_DEPTH_ONLY

                                else -> Config.DepthMode.DISABLED
                            }
                            cfg.instantPlacementMode = Config.InstantPlacementMode.DISABLED
                            cfg.lightEstimationMode = Config.LightEstimationMode.ENVIRONMENTAL_HDR
                            cfg.planeFindingMode = Config.PlaneFindingMode.VERTICAL
                        },
                        onSessionFailed = {
                            scope.launch {
                                ready = false
                                awaitFrame()
                                if (viewSize.width > 0 && viewSize.height > 0) {
                                    ready = true; sceneKey++
                                }
                            }
                        },
                        onSessionUpdated = { session, updated ->
                            if (!ready || isDisposing) return@ARScene
                            frame = updated
                            val cam = updated.camera

                            if (cam.trackingState != TrackingState.TRACKING) {
                                trackingFailureReason = cam.trackingFailureReason
                                centerHasPlane = false
                                liveLine?.isVisible = false
                                liveDot?.isVisible = false
                                return@ARScene
                            } else {
                                trackingFailureReason = null
                            }

                            val cx = viewSize.width * 0.5f
                            val cy = viewSize.height * 0.5f
                            if (cx <= 0f || cy <= 0f) return@ARScene

                            val planeHit = runCatching {
                                updated.hitTest(cx, cy)
                                    .filter { hr ->
                                        val pl = hr.trackable as? Plane ?: return@filter false
                                        pl.type == Plane.Type.VERTICAL &&
                                                pl.trackingState == TrackingState.TRACKING &&
                                                pl.isPoseInPolygon(hr.hitPose)
                                    }
                                    .minByOrNull { it.distance }
                            }.getOrNull()

                            centerHasPlane = planeHit != null
                            trackingFailureReason = TrackingFailureReason.NONE
                            val centerWorld = planeHit?.posePosition()

                            if (centerWorld != null) {
                                if (liveDot == null) {
                                    liveDot = SphereNode(
                                        engine = engine,
                                        radius = 0.015f
                                    ).apply { isShadowCaster = false }
                                    nodes += liveDot!!
                                }
                                liveDot!!.position = centerWorld
                                liveDot!!.isVisible = true
                            } else {
                                liveDot?.isVisible = false
                            }

                            liveDistance = if (centerWorld != null && lastPoint != null) {
                                distance(lastPoint!!, centerWorld)
                            } else null

                            val a = lastPoint
                            val b = centerWorld
                            if (a != null && b != null) {
                                if (liveLine == null) {
                                    liveLine = CylinderNode(
                                        engine = engine,
                                        radius = 0.008f,
                                        height = BASE_LINE_HEIGHT
                                    ).apply { isShadowCaster = false }
                                    nodes += liveLine!!
                                }
                                val verticalEndPoint = Position(a.x, b.y, a.z)
                                updateCylinderBetween3D(liveLine!!, a, verticalEndPoint)
                                liveLine!!.isVisible = true
                            } else {
                                liveLine?.isVisible = false
                            }
                        },
                        onTouchEvent = { _: MotionEvent, _: io.github.sceneview.collision.HitResult? -> false },
                        planeRenderer = true
                    )
                }
            }

            if (ready) {
                if (centerHasPlane) {
                    CenterReticle(Modifier.align(Alignment.Center), isVertical = true)
                    Box(
                        Modifier
                            .align(Alignment.TopCenter)
                            .padding(top = 12.dp)
                    ) {
                        Surface(color = Color(0x332196F3), shape = MaterialTheme.shapes.small) {
                            Text(
                                "벽 감지됨",
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                color = Color(0xFF1565C0),
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                    }
                }
            }

            // ⭐ 포인트 추가 버튼 (2개가 되면 자동 측정/종료)
            YeobaegButton(
                text = when {
                    segments.isEmpty() -> "벽 하단 포인트 추가"
                    else -> "벽 상단 포인트 추가"
                },
                onClick = {
                    val f = frame ?: return@YeobaegButton
                    if (f.camera.trackingState != TrackingState.TRACKING) return@YeobaegButton
                    if (!canAddPoint) return@YeobaegButton

                    val cx = viewSize.width * 0.5f
                    val cy = viewSize.height * 0.5f
                    if (cx <= 0f || cy <= 0f) return@YeobaegButton

                    val planeHit = runCatching {
                        f.hitTest(cx, cy).firstOrNull { hr ->
                            val pl = hr.trackable as? Plane ?: return@firstOrNull false
                            pl.type == Plane.Type.VERTICAL && pl.isPoseInPolygon(hr.hitPose)
                        }
                    }.getOrNull() ?: return@YeobaegButton

                    val world = planeHit.posePosition()
                    viewModel.addCorner(world, planeHit.trackable as? Plane)

                    val anchor = planeHit.createAnchor() ?: return@YeobaegButton
                    val anchorNode = AnchorNode(engine, anchor = anchor)
                    val marker =
                        SphereNode(engine = engine, radius = 0.01f).apply { isShadowCaster = false }
                    anchorNode.addChildNode(marker)
                    nodes += anchorNode

                    lastPoint?.let { prev ->
                        val len = distance(prev, world)
                        val line = CylinderNode(
                            engine = engine,
                            radius = 0.003f,
                            height = BASE_LINE_HEIGHT
                        )
                            .apply { isShadowCaster = false }
                        updateCylinderBetween3D(line, prev, world)
                        nodes += line
                        segments += Seg(anchorNode, marker, line, len, world)
                    } ?: run {
                        segments += Seg(anchorNode, marker, null, 0f, world)
                    }
                    lastPoint = world

                    // ⭐ 2개 포인트 찍히면 자동 완료
                    if (segments.size == 2) {
                        val h = distance(segments[0].world, segments[1].world)
                        scope.launch {
                            onMeasured(h * 100)
                            disposeWithSceneAlive()
                            awaitFrame()
                            onCancel()
                        }
                    }
                },
                enabled = ready && centerHasPlane && canAddPoint,
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 24.dp, start = 20.dp, end = 20.dp)
            )

            val reasonMessage = failReasonToString(trackingFailureReason)


            // HUD (실시간/마지막 거리 표시)
            DistanceHUD(
                message = reasonMessage,
                liveMeters = liveDistance,
                onUndo = {
                    if (segments.isNotEmpty()) {
                        val last = segments.removeAt(segments.lastIndex)
                        last.line?.let { removeNodeSafe(it, nodes) }
                        removeNodeSafe(last.marker, nodes)
                        detachAnchorSafe(last.anchorNode)
                        removeNodeSafe(last.anchorNode, nodes)
                        lastPoint = segments.lastOrNull()?.world
                        viewModel.undoCorner()
                    }
                },
                onClear = { clearInScene() }
            )
        }
    }
}
