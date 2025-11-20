package com.ssafy.yeobaeg.presentation.arcore.edit

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.yeobaeg.domain.model.DisplaySlot
import com.ssafy.yeobaeg.domain.model.SlotStatus
import com.ssafy.yeobaeg.domain.repository.CafeRepository
import com.ssafy.yeobaeg.navigation.BottomNavScreen
import com.ssafy.yeobaeg.navigation.navigator.Navigator
import com.ssafy.yeobaeg.presentation.common.util.SnackBarController
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class WallEditStep { SelectWall, MeasureHeight }

data class WallEditUiState(
    val step: WallEditStep = WallEditStep.SelectWall,
    val walls: List<DisplaySlot> = emptyList(),
    val selectedWallIds: Set<Long> = emptySet(),
    val targetIds: List<Long> = emptyList(),
    val currentWallId: Long? = null,
) {
    val currentIndex: Int
        get() = currentWallId?.let { id -> targetIds.indexOf(id) } ?: -1

    val allMeasured: Boolean
        get() {
            if (targetIds.isEmpty()) return false
            return targetIds.all { id ->
                val wall = walls.firstOrNull { it.wid == id }
                wall?.height != null && wall.height > 0f
            }
        }

    val goMeasureEnabled get() = walls.any { it.status != SlotStatus.UNAVAILABLE }

    fun currentWallOrNull(): DisplaySlot? =
        currentWallId?.let { id -> walls.firstOrNull { it.wid == id } }
}

@HiltViewModel
class WallEditViewModel @Inject constructor(
    private val cafeRepository: CafeRepository,
    private val navigator: Navigator,
    private val snackbarController: SnackBarController
) : ViewModel() {

    private val _uiState = MutableStateFlow(WallEditUiState())
    val uiState: StateFlow<WallEditUiState> = _uiState.asStateFlow()

    fun setInitialWalls(walls: List<DisplaySlot>) {
        _uiState.update { it.copy(walls = walls) }
    }

    fun onWallTapped(wallId: Long) {
        _uiState.update { s ->
            val idx = s.walls.indexOfFirst { it.wid == wallId }
            if (idx < 0) return@update s

            val w = s.walls[idx]
            val toggled = when (w.status) {
                SlotStatus.UNAVAILABLE -> SlotStatus.AVAILABLE
                SlotStatus.AVAILABLE, SlotStatus.EXHIBITING, SlotStatus.RESERVED-> SlotStatus.UNAVAILABLE
            }

            val nextWalls = s.walls.toMutableList().also { it[idx] = w.copy(status = toggled) }
            val nextSelected = s.selectedWallIds.toMutableSet().also {
                if (toggled != SlotStatus.UNAVAILABLE) it.add(wallId) else it.remove(wallId)
            }
            s.copy(walls = nextWalls, selectedWallIds = nextSelected)
        }
    }

    fun updateWallName(wallId: Long, newName: String) {
        _uiState.update { s ->
            val idx = s.walls.indexOfFirst { it.wid == wallId }
            if (idx < 0) s else {
                val nextList = s.walls.toMutableList()
                nextList[idx] = nextList[idx].copy(wName = newName)
                s.copy(walls = nextList)
            }
        }
    }

    fun selectWall(wallId: Long) {
        _uiState.update { s ->
            val w = s.walls.firstOrNull { it.wid == wallId } ?: return@update s
            if (w.status == SlotStatus.UNAVAILABLE) return@update s
            s.copy(currentWallId = wallId)
        }
    }

    fun proceedToMeasure() {
        _uiState.update { s ->
            val selectableIds = s.walls
                .filter { it.status != SlotStatus.UNAVAILABLE }
                .map { it.wid }

            val targets = if (s.selectedWallIds.isNotEmpty())
                s.selectedWallIds.filter { it in selectableIds }
            else
                selectableIds

            s.copy(
                step = WallEditStep.MeasureHeight,
                targetIds = targets,
                currentWallId = targets.firstOrNull()
            )
        }
    }

    /** ✅ 벽의 height 속성 업데이트 */
    fun saveMeasuredHeight(wallId: Long, heightM: Float) {
        _uiState.update { s ->
            val idx = s.walls.indexOfFirst { it.wid == wallId }
            if (idx < 0) return@update s

            val nextWalls = s.walls.toMutableList()
            nextWalls[idx] = nextWalls[idx].copy(height = heightM)
            s.copy(walls = nextWalls)
        }
    }

    /** ✅ 다음 측정 안된 벽으로 이동 */
    fun goNext() {
        _uiState.update { s ->
            if (s.targetIds.isEmpty()) return@update s

            val currentIdx = s.currentIndex
            if (currentIdx < 0) {
                // 현재 선택 없음 → 첫 번째 측정 안된 벽으로
                val firstUnmeasured = s.targetIds.firstOrNull { id ->
                    val wall = s.walls.firstOrNull { it.wid == id }
                    wall?.height == null || wall.height <= 0f
                }
                return@update s.copy(currentWallId = firstUnmeasured ?: s.targetIds.firstOrNull())
            }

            // 현재 위치 다음부터 끝까지 검색
            var i = currentIdx + 1
            while (i < s.targetIds.size) {
                val id = s.targetIds[i]
                val w = s.walls.firstOrNull { it.wid == id }
                val selectable = w?.status != SlotStatus.UNAVAILABLE
                val unmeasured = w?.height == null || w.height <= 0f

                if (selectable && unmeasured) {
                    return@update s.copy(currentWallId = id)
                }
                i++
            }

            // 뒤에 없으면 처음부터 현재 위치까지 검색 (순환)
            i = 0
            while (i < currentIdx) {
                val id = s.targetIds[i]
                val w = s.walls.firstOrNull { it.wid == id }
                val selectable = w?.status != SlotStatus.UNAVAILABLE
                val unmeasured = w?.height == null || w.height <= 0f

                if (selectable && unmeasured) {
                    return@update s.copy(currentWallId = id)
                }
                i++
            }

            // 모두 측정됨 → 현재 위치 유지
            s
        }
    }

    fun applyCurrentHeightToAll() {
        _uiState.update { s ->
            val currentWall = s.currentWallOrNull() ?: return@update s
            if (currentWall.height <= 0f) return@update s

            val nextWalls = s.walls.map { wall ->
                // targetIds에 포함된 벽들만 높이 적용
                if (wall.wid in s.targetIds) {
                    wall.copy(height = currentWall.height)
                } else {
                    wall
                }
            }
            s.copy(walls = nextWalls)
        }
    }

    fun prevOrBackToSelect() {
        if (_uiState.value.step == WallEditStep.SelectWall) {
            navigateToHome()
            return
        }
        _uiState.update { s ->
            s.copy(step = WallEditStep.SelectWall, currentWallId = null)
        }
    }

    fun navigateToHome() = viewModelScope.launch {
        navigator.navigateAndClearBackStack(BottomNavScreen.Home)
    }

    fun registerSlot() {
        viewModelScope.launch {
            cafeRepository.registerCafeWall(_uiState.value.walls).onSuccess {
                snackbarController.showSuccess("등록 성공")
                navigator.navigateAndClearBackStack(BottomNavScreen.Home)
            }.onFailure {
                Log.d("http registerSlot", it.message ?: "")
                snackbarController.showError(it.message ?: "오류")
            }
        }
    }
}