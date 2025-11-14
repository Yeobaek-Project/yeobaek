package com.ssafy.yeobaeg.presentation.cafe

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.yeobaeg.analytics.AnalyticsLogger
import com.ssafy.yeobaeg.domain.model.UserRole
import com.ssafy.yeobaeg.domain.model.cafe.CafeDetail
import com.ssafy.yeobaeg.domain.repository.CafeRepository
import com.ssafy.yeobaeg.domain.repository.ChatRepository
import com.ssafy.yeobaeg.domain.session.AuthSessionManager
import com.ssafy.yeobaeg.navigation.FullScreen
import com.ssafy.yeobaeg.navigation.navigator.Navigator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CafeDetailUiState(
    val cafeDetail: CafeDetail? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val selectedTab: CafeDetailTab = CafeDetailTab.EXHIBITION,
    val userRole: UserRole? = null
)

enum class CafeDetailTab {
    EXHIBITION,  // 전시
    INFO         // 정보
}

private const val TAG = "CafeDetailViewModel"
@HiltViewModel
class CafeDetailViewModel @Inject constructor(
    private val cafeRepository: CafeRepository,
    private val chatRepository: ChatRepository,
    private val navigator: Navigator,
    private val analyticsLogger: AnalyticsLogger,
    private val authSessionManager: AuthSessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(CafeDetailUiState())
    val uiState: StateFlow<CafeDetailUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            authSessionManager.currentUserRole.collect { role ->
                _uiState.update { it.copy(userRole = role) }
            }
        }
    }
    
    /**
     * 카페 상세 정보 로드
     */
    fun loadCafeDetail(cafeId: Long) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            cafeRepository.getCafeDetail(cafeId)
                .onSuccess { detail ->
                    Log.d("CafeDetailViewModel", "Loaded cafe detail with ${detail.displaySlots.size} slots")
                    detail.displaySlots.forEach { slot ->
                        Log.d("CafeDetailViewModel", "Slot: wid=${slot.wid}, name=${slot.wName}, width=${slot.width}, height=${slot.height}, commission=${slot.commission}, requirements=${slot.requirements}")
                    }
                    _uiState.update {
                        it.copy(
                            cafeDetail = detail,
                            isLoading = false
                        )
                    }
                    analyticsLogger.logCafeClick(detail.cafeId)
                }
                .onFailure { e ->
                    Log.e("CafeDetailViewModel", "Failed to load cafe detail", e)
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = e.message ?: "카페 정보를 불러올 수 없습니다"
                        )
                    }
                }
        }
    }
    
    /**
     * 탭 변경
     */
    fun selectTab(tab: CafeDetailTab) {
        _uiState.update { it.copy(selectedTab = tab) }
    }
    
    /**
     * 북마크 토글
     */
    fun toggleBookmark() {
        val cafeId = _uiState.value.cafeDetail?.cafeId ?: return

        viewModelScope.launch {
            // 낙관적 업데이트 (UI 즉시 반영)
            val currentBookmarkState = _uiState.value.cafeDetail?.isBookmarked ?: false
            _uiState.update {
                it.copy(
                    cafeDetail = it.cafeDetail?.copy(
                        isBookmarked = !currentBookmarkState
                    )
                )
            }

            // API 호출
            cafeRepository.toggleCafeBookmark(cafeId)
                .onSuccess { isBookmarked ->
                    // API 응답으로 최종 상태 업데이트
                    _uiState.update {
                        it.copy(
                            cafeDetail = it.cafeDetail?.copy(
                                isBookmarked = isBookmarked
                            )
                        )
                    }
                    if(isBookmarked) {
                        analyticsLogger.logCafeLike(cafeId)
                    }
                    else {
                        analyticsLogger.logCafeUnlike(cafeId)
                    }
                }
                .onFailure { e ->
                    Log.e("CafeDetailViewModel", "Failed to toggle bookmark", e)
                    // 실패 시 원래 상태로 롤백
                    _uiState.update {
                        it.copy(
                            cafeDetail = it.cafeDetail?.copy(
                                isBookmarked = currentBookmarkState
                            ),
                            error = "북마크 처리에 실패했습니다"
                        )
                    }
                }
        }
    }

    fun onInquiryClick() {
        val cafeId = _uiState.value.cafeDetail?.cafeId ?: return

        viewModelScope.launch {
            chatRepository.createChatRoom(cafeId)
                .onSuccess { room ->
                    navigator.navigate(
                        FullScreen.ChattingScreen(room.chatRoomId)
                    )
                }
                .onFailure { e ->
                    Log.d(TAG, "onInquiryClick: 채팅방 생성 실패")
                }
        }
    }

    fun navigateBack() = viewModelScope.launch {
        navigator.navigateBack()
    }

    /**
     * 전시 캘린더로 이동
     */
    fun navigateToExhibitionCalendar(slot: com.ssafy.yeobaeg.domain.model.DisplaySlot) {
        viewModelScope.launch {
            Log.d("CafeDetailViewModel", "Navigating to calendar with slot: wid=${slot.wid}, name=${slot.wName}, width=${slot.width}, height=${slot.height}, commission=${slot.commission}, requirements=${slot.requirements}")
            navigator.navigate(
                route = FullScreen.ExhibitionCalendar(
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

    /**
     * 에러 메시지 클리어
     */
    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    fun logViewEnd(id: Long, dwell: Long, maxPct: Int) {
        Log.d("logViewEnd cafe", "$id / $dwell / $maxPct")
        analyticsLogger.logCafeViewEnd(cafeId = id, dwellMs = dwell, scrollPct = maxPct)
    }
}

