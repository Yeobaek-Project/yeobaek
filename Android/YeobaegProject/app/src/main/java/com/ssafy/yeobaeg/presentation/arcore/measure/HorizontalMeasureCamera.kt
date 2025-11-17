package com.ssafy.yeobaeg.presentation.arcore.horizontal

import android.view.MotionEvent
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.ar.core.Config
import com.google.ar.core.Frame
import com.google.ar.core.Plane
import com.google.ar.core.Session
import com.google.ar.core.TrackingFailureReason
import com.google.ar.core.TrackingState
import com.ssafy.yeobaeg.R
import com.ssafy.yeobaeg.presentation.arcore.common.createCubeBetweenGround
import com.ssafy.yeobaeg.presentation.arcore.common.detachAnchorSafe
import com.ssafy.yeobaeg.presentation.arcore.common.distance
import com.ssafy.yeobaeg.presentation.arcore.common.fmt
import com.ssafy.yeobaeg.presentation.arcore.common.posePosition
import com.ssafy.yeobaeg.presentation.arcore.common.removeNodeSafe
import com.ssafy.yeobaeg.presentation.arcore.common.updateCylinderBetweenGround
import com.ssafy.yeobaeg.presentation.arcore.measure.Seg
import com.ssafy.yeobaeg.presentation.common.component.YeobaegButton
import com.ssafy.yeobaeg.ui.theme.YeobaegGray
import com.ssafy.yeobaeg.ui.theme.YeobaegLightBrown
import io.github.sceneview.ar.ARScene
import io.github.sceneview.ar.node.AnchorNode
import io.github.sceneview.ar.rememberARCameraStream
import io.github.sceneview.math.Position
import io.github.sceneview.node.CubeNode
import io.github.sceneview.node.CylinderNode
import io.github.sceneview.node.SphereNode
import io.github.sceneview.rememberEngine
import io.github.sceneview.rememberMaterialLoader
import io.github.sceneview.rememberNodes
import kotlinx.coroutines.android.awaitFrame
import kotlinx.coroutines.launch

private const val MIN_POINT_GAP_M = 0.10f
private const val BASE_LINE_HEIGHT = 1.0f

@Composable
fun HorizontalMeasureCamera(
    onCancel: () -> Unit = {},
    viewModel: HorizontalMeasureViewModel = hiltViewModel()
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

    val state by viewModel.uiState.collectAsState()

    var trackingFailureReason by remember { mutableStateOf<TrackingFailureReason?>(null) }

    LaunchedEffect(viewSize) {
        ready = if (viewSize.width > 0 && viewSize.height > 0) {
            awaitFrame(); true
        } else false
    }

    // Scene 정리 헬퍼
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
        Box(
            Modifier
                .fillMaxSize()
                .onSizeChanged { viewSize = it }
        ) {
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
                            cfg.depthMode =
                                if (s.isDepthModeSupported(Config.DepthMode.AUTOMATIC))
                                    Config.DepthMode.AUTOMATIC
                                else Config.DepthMode.DISABLED
                            cfg.instantPlacementMode = Config.InstantPlacementMode.DISABLED
                            cfg.lightEstimationMode = Config.LightEstimationMode.ENVIRONMENTAL_HDR
                            cfg.planeFindingMode = Config.PlaneFindingMode.HORIZONTAL
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
                        onSessionUpdated = { _, updated ->
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
                                updated.hitTest(cx, cy).firstOrNull { hr ->
                                    val pl = hr.trackable as? Plane ?: return@firstOrNull false
                                    pl.type == Plane.Type.HORIZONTAL_UPWARD_FACING &&
                                            pl.isPoseInPolygon(hr.hitPose)
                                }
                            }.getOrNull()
                            centerHasPlane = planeHit != null

                            val centerWorld = planeHit?.posePosition()

                            // live dot
                            if (centerWorld != null) {
                                if (liveDot == null) {
                                    liveDot = SphereNode(
                                        engine = engine,
                                        radius = 0.006f
                                    ).apply { isShadowCaster = false }
                                    nodes += liveDot!!
                                }
                                liveDot!!.position = centerWorld
                                liveDot!!.isVisible = true
                            } else liveDot?.isVisible = false

                            // live line + 거리
                            liveDistance = if (centerWorld != null && lastPoint != null) {
                                distance(lastPoint!!, centerWorld)
                            } else null

                            val a = lastPoint
                            val b = centerWorld
                            if (a != null && b != null) {
                                if (liveLine == null) {
                                    liveLine = CylinderNode(
                                        engine = engine,
                                        radius = 0.006f,
                                        height = BASE_LINE_HEIGHT
                                    )
                                        .apply { isShadowCaster = false }
                                    nodes += liveLine!!
                                }
                                updateCylinderBetweenGround(liveLine!!, a, b)
                                liveLine!!.isVisible = true
                            } else liveLine?.isVisible = false
                        },
                        onTouchEvent = { _: MotionEvent, _: io.github.sceneview.collision.HitResult? -> false },
                        planeRenderer = true
                    )
                }
            }

            if (ready && centerHasPlane) {
                CenterReticle(Modifier.align(Alignment.Center), isVertical = false)
            }

            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 24.dp, start = 20.dp, end = 20.dp)
            ) {
                YeobaegButton(
                    text = if (state.isBuilding) "계산 중…" else "도면 생성",
                    onClick = {
                        scope.launch {
                            disposeWithSceneAlive()
                            awaitFrame()
                            viewModel.buildAndNavigate()
                        }
                    },
                    enabled = ready && !state.isBuilding && state.cornersWorld.size >= 3,
                    modifier = Modifier.weight(1f)
                )
                Spacer(Modifier.width(24.dp))
                // 포인트 추가
                YeobaegButton(
                    text = "포인트 추가",
                    onClick = {
                        val f = frame ?: return@YeobaegButton
                        if (f.camera.trackingState != TrackingState.TRACKING) return@YeobaegButton

                        val cx = viewSize.width * 0.5f
                        val cy = viewSize.height * 0.5f
                        if (cx <= 0f || cy <= 0f) return@YeobaegButton

                        val planeHit = runCatching {
                            f.hitTest(cx, cy).firstOrNull { hr ->
                                val pl = hr.trackable as? Plane ?: return@firstOrNull false
                                pl.type == Plane.Type.HORIZONTAL_UPWARD_FACING &&
                                        pl.isPoseInPolygon(hr.hitPose)
                            }
                        }.getOrNull() ?: return@YeobaegButton

                        val world = planeHit.posePosition()

                        // 최소 간격
                        if (lastPoint != null && distance(
                                lastPoint!!,
                                world
                            ) < MIN_POINT_GAP_M
                        ) return@YeobaegButton

                        viewModel.addCorner(world, planeHit.trackable as? Plane)

                        val anchor = planeHit.createAnchor() ?: return@YeobaegButton
                        val anchorNode = AnchorNode(engine, anchor = anchor)
                        val marker =
                            SphereNode(engine = engine, radius = 0.01f).apply {
                                isShadowCaster = false
                            }
                        anchorNode.addChildNode(marker)
                        nodes += anchorNode

                        lastPoint?.let { prev ->
                            val len = distance(prev, world)
                            val line = createCubeBetweenGround(materialLoader, prev, world)
                            nodes += line
                            segments += Seg(anchorNode, marker, line, len, world)
                        } ?: run {
                            segments += Seg(anchorNode, marker, null, 0f, world)
                        }
                        lastPoint = world
                    },
                    enabled = ready && centerHasPlane,
                    modifier = Modifier.weight(1f)
                )
            }

            val reasonMessage = failReasonToString(trackingFailureReason)

            // HUD
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

                        if (segments.isEmpty()) {
                            lastPoint = null
                        } else {
                            lastPoint = segments.last().world
                        }
                        viewModel.undoCorner()
                    }
                },
                onClear = { clearInScene() }
            )
        }
    }
}

@Composable
fun DistanceHUD(
    message: String?, // nullable String으로 변경
    liveMeters: Float?,
    onUndo: () -> Unit,
    onClear: () -> Unit
) {
    Box(
        Modifier
            .fillMaxWidth()
            .padding(20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top // 버튼과 중앙 컨텐츠의 상단을 맞춤
        ) {
            // Undo 버튼 (기존 코드와 동일)
            IconButton(
                onClick = onUndo,
                modifier = Modifier
                    .clip(CircleShape)
                    .background(YeobaegLightBrown)
            ) {
                Icon(
                    painterResource(R.drawable.ic_undo),
                    contentDescription = "Undo Button",
                    tint = Color.White
                )
            }

            Spacer(Modifier.weight(1f))

            // Clear(초기화) 버튼 (기존 코드와 동일)
            IconButton(
                onClick = onClear,
                modifier = Modifier
                    .clip(CircleShape)
                    .background(YeobaegLightBrown)
            ) {
                Icon(
                    // painterResource(R.drawable.ic_replay),
                    // 예시 아이콘. 실제 프로젝트의 리소스로 교체하세요.
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Clear Button",
                    tint = Color.White
                )
            }
        }

        // message가 있을 때만 상태 메시지 표시
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 80.dp, start = 32.dp, end = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = liveMeters?.let { fmt(it * 100) } ?: "-",
                fontSize = 32.sp,
            )
            Spacer(Modifier.height(12.dp))
            Text(
                message ?: "측정할 지점에 앵커를 생성해 주세요.", color = YeobaegGray,
                textAlign = TextAlign.Center
            )
        }
    }

}

@Composable
fun CenterReticle(modifier: Modifier, isVertical: Boolean) {
    val color = if (isVertical) Color(0xFF2196F3) else Color(0xFF4CAF50)
    Box(
        modifier
            .size(22.dp)
            .background(color.copy(alpha = 0.2f), CircleShape)
            .border(2.dp, color, CircleShape)
    )
}


@Composable
fun failReasonToString(trackingFailureReason: TrackingFailureReason?): String? {
    val reasonMessage = when (trackingFailureReason) {
        TrackingFailureReason.BAD_STATE ->
            "트래킹 상태가 불안정합니다. 앱을 다시 시작해 주세요."

        TrackingFailureReason.CAMERA_UNAVAILABLE ->
            "다른 앱에서 카메라를 사용하고 있습니다."

        TrackingFailureReason.EXCESSIVE_MOTION ->
            "너무 빨리 움직이고 있습니다. 천천히 움직여 주세요."

        TrackingFailureReason.INSUFFICIENT_FEATURES ->
            "평면의 무늬나 질감이 부족합니다. 특징이 있는 곳을 비춰 주세요."

        TrackingFailureReason.INSUFFICIENT_LIGHT ->
            "조명이 부족합니다. 더 밝은 곳으로 이동해 주세요."

        TrackingFailureReason.NONE -> "평면을 천천히 스캔하세요."

        else -> null
    }
    return reasonMessage
}