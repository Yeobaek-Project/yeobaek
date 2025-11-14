// presentation/artwork/detail/ArtworkDetailViewModel.kt
package com.ssafy.yeobaeg.presentation.artwork.detail

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.yeobaeg.analytics.AnalyticsLogger
import com.ssafy.yeobaeg.domain.model.UserRole
import com.ssafy.yeobaeg.domain.model.art.ArtworkDetail
import com.ssafy.yeobaeg.domain.repository.ArtRepository
import com.ssafy.yeobaeg.domain.repository.ChatRepository
import com.ssafy.yeobaeg.domain.session.AuthSessionManager
import com.ssafy.yeobaeg.navigation.FullScreen
import com.ssafy.yeobaeg.navigation.navigator.Navigator
import com.ssafy.yeobaeg.presentation.common.util.SnackBarController
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ArtworkDetailUiState(
    val loading: Boolean = true,
    val error: String? = null,
    val artwork: ArtworkDetail? = null,
    val userRole: UserRole? = null
)

@HiltViewModel
class ArtworkDetailViewModel @Inject constructor(
    private val artRepository: ArtRepository,
    private val chatRepository: ChatRepository,
    private val navigator: Navigator,
    private val snackBarController: SnackBarController,
    private val analyticsLogger: AnalyticsLogger,
    private val authSessionManager: AuthSessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(ArtworkDetailUiState())
    val uiState: StateFlow<ArtworkDetailUiState> = _uiState.asStateFlow()

    private var toggleJob: Job? = null

    init {
        viewModelScope.launch {
            authSessionManager.currentUserRole.collect { role ->
                _uiState.update { it.copy(userRole = role) }
            }
        }
    }

    fun onBackClick() = viewModelScope.launch {
        navigator.navigateBack()
    }

    fun load(artworkId: Long) {
        _uiState.update { it.copy(loading = true) }
        viewModelScope.launch {
            artRepository.getArtworkDetail(artworkId)
                .onSuccess { detail ->
                    _uiState.update { it.copy(loading = false, artwork = detail) }
                    analyticsLogger.logArtworkClick(detail.artworkId)
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            loading = false,
                            error = error.message ?: "알 수 없는 오류"
                        )
                    }
                    snackBarController.showError(error.message ?: "오류")
                }
        }
    }

    fun toggleBookmark() {
        val d = _uiState.value.artwork ?: return
        val b = d.isBookmarked

        toggleJob?.cancel()

        toggleJob = viewModelScope.launch {
            _uiState.update { it.copy(artwork = d.copy(isBookmarked = !b)) }
            artRepository.toggleBookmark(d.artworkId).onSuccess { response ->
                _uiState.update { it.copy(artwork = d.copy(isBookmarked = response)) }
                if(response) {
                    analyticsLogger.logArtworkLike(d.artworkId)
                }
                else {
                    analyticsLogger.logArtworkUnlike(d.artworkId)
                }
            }.onFailure {
                _uiState.update { it.copy(artwork = d.copy(isBookmarked = b)) }
                snackBarController.showError(it.message ?: "오류")
            }
        }
    }

    fun onInquiryClick() {
        val artistId = _uiState.value.artwork?.artist?.artistId ?: return

        viewModelScope.launch {
            chatRepository.createChatRoom(artistId)
                .onSuccess { room ->
                    navigator.navigate(
                        FullScreen.ChattingScreen(room.chatRoomId)
                    )
                }
                .onFailure { e ->
                    snackBarController.showError(e.message ?: "채팅방 생성 실패")
                }
        }
    }

    fun navigateToArtist() = viewModelScope.launch {
        val artistId = _uiState.value.artwork?.artist?.artistId ?: return@launch
        navigator.navigate(FullScreen.ArtistProfile(artistId))
    }

    fun logViewEnd(id: Long, dwell: Long, maxPct: Int) {
        Log.d("logViewEnd art", "$id / $dwell / $maxPct")
        analyticsLogger.logArtworkViewEnd(artworkId = id, dwellMs = dwell, scrollPct = maxPct)
    }
}
