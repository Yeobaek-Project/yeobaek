package com.ssafy.yeobaeg.presentation.home.cafeowner.matching

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.yeobaeg.domain.model.DisplaySlot
import com.ssafy.yeobaeg.domain.model.Pagination
import com.ssafy.yeobaeg.domain.model.SlotStatus
import com.ssafy.yeobaeg.domain.model.profile.ArtworkSummary
import com.ssafy.yeobaeg.domain.repository.ArtRepository
import com.ssafy.yeobaeg.navigation.FullScreen
import com.ssafy.yeobaeg.navigation.navigator.Navigator
import com.ssafy.yeobaeg.presentation.home.artist.matching.MatchingStep
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class MatchingMode {
    SELECT_SLOT,      // 슬롯 선택
    SELECT_ARTWORK,   // 작품 선택
    LOADING_RECOMMEND
}

data class MatchingUiState(
    val mode: MatchingMode = MatchingMode.SELECT_SLOT,
    val slots: List<DisplaySlot> = emptyList(),
    val currentWallId: Long = -1L,

    // 작품 관련
    val isLoadingArtworks: Boolean = false,
    val isLoadingMoreArtworks: Boolean = false,
    val recommendedArtworks: List<ArtworkSummary> = emptyList(),
    val artworkPagination: Pagination? = null,
    val selectedArtworkId: Long? = null,
    val selectedArtwork: ArtworkSummary? = null
)

@HiltViewModel
class CafeOwnerMatchingViewModel @Inject constructor(
    private val artRepository: ArtRepository,
    private val navigator: Navigator
) : ViewModel() {

    private var _uiState = MutableStateFlow(MatchingUiState())
    val uiState = _uiState.asStateFlow()

    fun initSlots(initSlots: List<DisplaySlot>) {
        _uiState.update {
            it.copy(slots = initSlots)
        }
    }

    fun selectWall(wallId: Long) {
        _uiState.update { s ->
            val w = s.slots.firstOrNull { it.wid == wallId } ?: return@update s
            if (w.status == SlotStatus.UNAVAILABLE) return@update s
            s.copy(currentWallId = wallId)
        }
    }

    fun backToSlotSelection() {
        _uiState.update {
            it.copy(
                mode = MatchingMode.SELECT_SLOT,
                recommendedArtworks = emptyList(),
                artworkPagination = null,
                selectedArtworkId = null,
                selectedArtwork = null,
                isLoadingArtworks = false,
                isLoadingMoreArtworks = false
            )
        }
    }

    fun confirmArtworkSelection() {
        val currentWallId = _uiState.value.currentWallId
        if (currentWallId == -1L) return // 선택된 슬롯 없으면 무시

        _uiState.update {
            it.copy(mode = MatchingMode.LOADING_RECOMMEND)
        }
        loadRecommendedArtworks()
    }

    // ========== 작품 관련 ==========
    private fun loadRecommendedArtworks() {
        val wallId = _uiState.value.currentWallId
        if (wallId == -1L) return

        viewModelScope.launch {
            val startTime = System.currentTimeMillis()

            // API 호출
            val apiResult = artRepository.getRecommendedArtworks(slotId = wallId)

            // 경과 시간 계산
            val elapsedTime = System.currentTimeMillis() - startTime
            val remainingTime = (5000 - elapsedTime).coerceAtLeast(0)

            // 최소 3초 보장
            if (remainingTime > 0) {
                delay(remainingTime)
            }

            // 결과 처리
            apiResult.onSuccess { data ->
                _uiState.update {
                    it.copy(
                        isLoadingArtworks = false,
                        recommendedArtworks = data.artworks,
                        artworkPagination = data.pagination,
                        mode = MatchingMode.SELECT_ARTWORK
                    )
                }
            }.onFailure {
                _uiState.update { it.copy(isLoadingArtworks = false) }
            }
        }
    }

    fun loadMoreArtworks() {
        val wallId = _uiState.value.currentWallId
        if (wallId == -1L) return

        val pagination = _uiState.value.artworkPagination ?: return
        if (_uiState.value.isLoadingMoreArtworks || !pagination.hasNext) return

        _uiState.update { it.copy(isLoadingMoreArtworks = true) }

        viewModelScope.launch {
            artRepository.getRecommendedArtworks(
                slotId = wallId,
                page = pagination.currentPage + 1,
                size = pagination.pageSize
            ).onSuccess { data ->
                _uiState.update {
                    it.copy(
                        isLoadingMoreArtworks = false,
                        recommendedArtworks = it.recommendedArtworks + data.artworks,
                        artworkPagination = data.pagination
                    )
                }
            }.onFailure {
                _uiState.update { it.copy(isLoadingMoreArtworks = false) }
            }
        }
    }

    fun navigateToArtworkDetail(artworkId: Long) = viewModelScope.launch {
        navigator.navigate(FullScreen.ArtworkDetail(artworkId))
    }

    fun navigateToBack() = viewModelScope.launch {
        navigator.navigateBack()
    }
}