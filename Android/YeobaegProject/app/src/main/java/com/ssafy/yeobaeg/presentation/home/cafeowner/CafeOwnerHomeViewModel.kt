package com.ssafy.yeobaeg.presentation.home.cafeowner

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ar.core.dependencies.g
import com.ssafy.yeobaeg.domain.model.DisplaySlot
import com.ssafy.yeobaeg.domain.repository.CafeRepository
import com.ssafy.yeobaeg.navigation.FullScreen
import com.ssafy.yeobaeg.navigation.navigator.Navigator
import com.ssafy.yeobaeg.presentation.common.util.SnackBarController
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CafeOwnerHomeViewModel @Inject constructor(
    private val cafeRepository: CafeRepository,
    private val navigator: Navigator,
    private val snackBarController: SnackBarController
) : ViewModel() {

    private var _uiState = MutableStateFlow(CafeOwnerHomeUiState())
    val uiState = _uiState.asStateFlow()

    fun loadMyCafe() {
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            cafeRepository.getMyCafe().onSuccess { data ->
                _uiState.update {
                    it.copy(cafeOwnerInfo = data, isLoading = false)
                }
            }.onFailure {
                Log.d("cafeOwnerHomeViewModel load error", it.message ?: "")
                _uiState.update { it.copy(isLoading = false) }
                snackBarController.showError(it.message ?: "오류")
            }
        }
    }

    fun selectTab(tab: CafeOwnerTab) {
        _uiState.update { it.copy(selectedTab = tab) }
    }

    fun navigateToSpaceScan() = viewModelScope.launch {
        navigator.navigate(
            FullScreen.SpaceScan
        )
    }

    fun navigateToMatching() = viewModelScope.launch {
        val cafe = _uiState.value.cafeOwnerInfo ?: return@launch
        navigator.navigate (FullScreen.CafeOwnerMatching(cafe.displaySlots))
    }

    fun navigateToRegister() = viewModelScope.launch {
        navigator.navigate(
            FullScreen.RegisterCafe
        )
    }

    fun navigateToSlot(slot: DisplaySlot) = viewModelScope.launch {
        _uiState.value.cafeOwnerInfo?.let {
            navigator.navigate(
                FullScreen.EditDisplaySlotScreen(
                    it.cafeId,
                    slot.wid,
                    slot.commission.toInt(),
                    slot.requirements
                )
            )
        }
    }

    fun navigateToExhibitionCalendar(slot: com.ssafy.yeobaeg.domain.model.DisplaySlot) = viewModelScope.launch {
        navigator.navigate(
            FullScreen.ExhibitionCalendar(
                slotId = slot.wid,
                slotName = slot.wName,
                width = slot.width,
                height = slot.height,
                commission = slot.commission,
                requirements = slot.requirements
            )
        )
    }
}
