package com.ssafy.yeobaeg.presentation.arcore.measure

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ar.core.Plane
import com.ssafy.yeobaeg.navigation.navigator.Navigator
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.sceneview.math.Position
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/** 공통 측정 뷰모델 베이스 */
open class MeasureBaseViewModel(
    protected val navigator: Navigator
) : ViewModel() {

    protected val _uiState = MutableStateFlow(MeasureState())
    val uiState: StateFlow<MeasureState> = _uiState

    /** 공통 포인트 추가 */
    open fun addCorner(world: Position, plane: Plane?) {
        _uiState.update { cur ->
            val planePose = cur.planePose ?: plane?.centerPose
            cur.copy(
                planePose = planePose,
                cornersWorld = cur.cornersWorld + world
            )
        }
    }

    /** 되돌리기 */
    open fun undoCorner() = _uiState.update { cur ->
        if (cur.cornersWorld.isEmpty()) cur
        else cur.copy(cornersWorld = cur.cornersWorld.dropLast(1))
    }

    /** 상태 초기화 */
    open fun clearAll() = _uiState.update {
        it.copy(planePose = null, cornersWorld = emptyList())
    }

    /** 빌드 플래그 */
    protected fun setBuilding(building: Boolean) = _uiState.update {
        it.copy(isBuilding = building)
    }

    /** 뒤로가기 공통 */
    open fun onBackPressed() = viewModelScope.launch { navigator.navigateBack() }
}
