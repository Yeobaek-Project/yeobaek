package com.ssafy.yeobaeg.presentation.arcore.edit

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ssafy.yeobaeg.domain.model.DisplaySlot
import com.ssafy.yeobaeg.domain.model.SlotStatus
import com.ssafy.yeobaeg.presentation.arcore.plan.FloorPlanPreview
import com.ssafy.yeobaeg.presentation.arcore.measure.VerticalMeasureCamera
import com.ssafy.yeobaeg.presentation.common.component.YeobaegButton
import com.ssafy.yeobaeg.ui.theme.YeobaegBlack

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WallEditScreen(
    initialWalls: List<DisplaySlot>,
    onClose: () -> Unit = {},
    viewModel: WallEditViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    var showCamera by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) { viewModel.setInitialWalls(initialWalls) }

    BackHandler {
        if (showCamera) {
            showCamera = false
        } else {
            viewModel.prevOrBackToSelect()
        }
    }

    Column(Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = viewModel::prevOrBackToSelect
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "뒤로가기",
                    tint = YeobaegBlack
                )
            }

            Text(
                text = when (state.step) {
                    WallEditStep.SelectWall -> "탭하여 전시 가능한 벽을 선택하세요"
                    WallEditStep.MeasureHeight -> {
                        val measuredCount = state.walls.count { wall ->
                            state.targetIds.contains(wall.wid) && wall.height > 0f
                        }
                        val totalCount = state.targetIds.size
                        "전시가능한 벽의 높이를 측정하세요 ($measuredCount/$totalCount)"
                    }
                },
                fontSize = 16.sp
            )
        }

        when (state.step) {
            WallEditStep.SelectWall -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp)
                ) {
                    FloorPlanPreview(
                        initialWalls = state.walls,
                        readOnly = false,
                        onWallTapped = { id -> viewModel.onWallTapped(id) },
                        showHelpText = true,
                        modifier = Modifier.weight(1f)
                    )
                    YeobaegButton(
                        text = "다음",
                        onClick = viewModel::proceedToMeasure,
                        enabled = state.goMeasureEnabled
                    )
                }

            }

            WallEditStep.MeasureHeight -> {
                if (showCamera) {
                    VerticalMeasureCamera(
                        modifier = Modifier.fillMaxSize(),
                        onMeasured = { heightM ->
                            val wid = state.currentWallId ?: return@VerticalMeasureCamera
                            viewModel.saveMeasuredHeight(wid, heightM)
                            showCamera = false
                        },
                        onCancel = { showCamera = false }
                    )
                } else {
                    FloorPlanPreview(
                        modifier = Modifier
                            .weight(1f)
                            .align(Alignment.CenterHorizontally)
                            .padding(20.dp),
                        initialWalls = state.walls,
                        readOnly = true,
                        onWallTapped = { id -> viewModel.selectWall(id) },
                        activeWallId = state.currentWallId,
                    )

                    val current = state.currentWallOrNull()
                    if (current != null) {
                        WallDetailSheet(
                            modifier = Modifier.fillMaxWidth(),
                            wall = current,
                            onNameChange = { newName ->
                                viewModel.updateWallName(current.wid, newName)
                            },
                            onMeasureClick = { showCamera = true },
                            onNextClick = {
                                if (state.allMeasured) {
                                    viewModel.registerSlot()
                                } else {
                                    viewModel.goNext()
                                }
                            },
                            allMeasured = state.allMeasured
                        )
                    }
                }
            }
        }
    }
}

private fun SlotStatus.isSelectable(): Boolean =
    this != SlotStatus.UNAVAILABLE