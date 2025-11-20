package com.ssafy.yeobaeg.presentation.exhibition

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.yeobaeg.domain.model.exhibition.ExhibitionSchedule
import com.ssafy.yeobaeg.domain.repository.ExhibitionRepository
import com.ssafy.yeobaeg.navigation.navigator.Navigator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

data class ExhibitionCalendarUiState(
    val schedules: List<ExhibitionSchedule> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val startDate: LocalDate = LocalDate.now(),
    val selectedDate: LocalDate? = null
)

@HiltViewModel
class ExhibitionCalendarViewModel @Inject constructor(
    private val exhibitionRepository: ExhibitionRepository,
    private val navigator: Navigator
) : ViewModel() {

    private val _uiState = MutableStateFlow(ExhibitionCalendarUiState())
    val uiState: StateFlow<ExhibitionCalendarUiState> = _uiState.asStateFlow()

    /**
     * 전시 일정 로드
     */
    fun loadSchedule(slotId: Long) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            val fromDate = LocalDate.now().format(formatter)

            exhibitionRepository.getSlotSchedule(slotId, fromDate)
                .onSuccess { schedules ->
                    _uiState.update {
                        it.copy(
                            schedules = schedules,
                            isLoading = false
                        )
                    }
                }
                .onFailure { e ->
                    Log.e("ExhibitionCalendarViewModel", "Failed to load schedule", e)
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = e.message ?: "전시 일정을 불러올 수 없습니다"
                        )
                    }
                }
        }
    }

    /**
     * 날짜 선택
     */
    fun selectDate(date: LocalDate) {
        _uiState.update { it.copy(selectedDate = date) }
    }

    /**
     * 날짜 선택 해제
     */
    fun clearSelectedDate() {
        _uiState.update { it.copy(selectedDate = null) }
    }

    /**
     * 뒤로 가기
     */
    fun navigateBack() = viewModelScope.launch {
        navigator.navigateBack()
    }
}
