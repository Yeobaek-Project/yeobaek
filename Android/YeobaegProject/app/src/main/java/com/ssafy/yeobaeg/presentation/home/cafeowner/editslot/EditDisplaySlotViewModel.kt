package com.ssafy.yeobaeg.presentation.home.cafeowner.editslot

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.yeobaeg.domain.repository.CafeRepository
import com.ssafy.yeobaeg.navigation.navigator.Navigator
import com.ssafy.yeobaeg.presentation.common.util.SnackBarController
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditDisplaySlotViewModel @Inject constructor(
    private val cafeRepository: CafeRepository,
    private val navigator: Navigator,
    private val snackBarController: SnackBarController
) : ViewModel() {
    private val _uiState = MutableStateFlow(EditSlotState())
    val uiState = _uiState.asStateFlow()

    private fun update(block: EditSlotState.() -> EditSlotState) {
        _uiState.value = _uiState.value.block()
    }

    fun initSlotId(
        cafeId: Long,
        slotId: Long,
        commissionRate: Int,
        requirements: String
    ) {
        update { copy(cafeId = cafeId, slotId = slotId, feePercent = commissionRate, note = requirements) }
    }

    fun updateDateRange(startMillis: Long?, endMillis: Long?) {
        if (startMillis != null && endMillis != null) {
            update { copy(startAtMillis = startMillis, endAtMillis = endMillis) }
        }
    }

    fun updateFeePercent(percent: Int) {
        update { copy(feePercent = percent) }
    }

    fun updateNote(text: String) {
        update { copy(note = text.take(500)) }
    }

    fun onBackPressed() = viewModelScope.launch {
        navigator.navigateBack()
    }

    fun patchSlotInfo() {
        viewModelScope.launch {
            cafeRepository.updateSlotInfo(
                _uiState.value.cafeId,
                _uiState.value.slotId,
                _uiState.value.feePercent,
                _uiState.value.note,
            ).onSuccess {
                navigateBack()
                snackBarController.showSuccess("수정 완료")
            }.onFailure {
                snackBarController.showError(it.message ?: "오류")
            }
        }
    }

    private fun navigateBack() = viewModelScope.launch {
        navigator.navigateBack()
    }
}

data class EditSlotState(
    val cafeId: Long = -1L,
    val slotId: Long = -1L,
    val startAtMillis: Long? = null,
    val endAtMillis: Long? = null,
    val feePercent: Int? = null,
    val note: String = ""
) {
    val isValid: Boolean = true
}