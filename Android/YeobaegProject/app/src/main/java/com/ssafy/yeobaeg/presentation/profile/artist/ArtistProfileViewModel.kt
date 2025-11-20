package com.ssafy.yeobaeg.presentation.profile.artist

import androidx.lifecycle.viewModelScope
import com.ssafy.yeobaeg.domain.model.Pagination
import com.ssafy.yeobaeg.domain.model.profile.ArtistInfo
import com.ssafy.yeobaeg.domain.model.profile.Artwork
import com.ssafy.yeobaeg.domain.model.profile.ArtworkSummary
import com.ssafy.yeobaeg.domain.model.profile.UserInfo
import com.ssafy.yeobaeg.domain.repository.ArtRepository
import com.ssafy.yeobaeg.domain.repository.ProfileRepository
import com.ssafy.yeobaeg.domain.session.AuthSessionManager
import com.ssafy.yeobaeg.navigation.navigator.Navigator
import com.ssafy.yeobaeg.presentation.profile.ProfileViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ArtistProfileUiState(
    val isLoading: Boolean = false,
    val artistInfo: ArtistInfo? = null,
    val userInfo: UserInfo = UserInfo(),
    val pagination: Pagination? = null,
    val artworks: List<ArtworkSummary> = emptyList(),
    val errorMessage: String? = null,
    val bookmarkCount: Int = 0
)

@HiltViewModel
class ArtistProfileViewModel @Inject constructor(
    private val navigator: Navigator,
    private val profileRepository: ProfileRepository,
    private val artRepository: ArtRepository,
    authSessionManager: AuthSessionManager
) : ProfileViewModel(navigator, authSessionManager) {
    private val _uiState = MutableStateFlow(ArtistProfileUiState())
    val uiState: StateFlow<ArtistProfileUiState> = _uiState.asStateFlow()

    fun loadArtistProfile(userId: Long?) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val result = if (userId == null) {
                profileRepository.getProfile() // 내 프로필
            } else {
                profileRepository.getSomeoneProfile(userId) // 다른 사람 프로필
            }

            result
                .onSuccess { profile ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            userInfo = profile.userInfo,
                            artistInfo = profile.artistInfo,
                        )
                    }

                    // 내 프로필인 경우에만 북마크 개수 로드
                    if (userId == null) {
                        loadBookmarkCount()
                    }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.message ?: "알 수 없는 오류"
                        )
                    }
                }
        }
    }

    fun loadArtworks(userId: Long? = null) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val result = if (userId == null) {
                artRepository.getMyArtworks()
            } else {
                artRepository.getUserArtworks(userId)
            }

            result
                .onSuccess { artwork ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            artworks = artwork.artworks,
                            pagination = artwork.pagination
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.message ?: "알 수 없는 오류"
                        )
                    }
                }
        }
    }

    /**
     * 북마크 총 개수 조회 (size=1로 조회해서 totalElements만 사용)
     */
    private fun loadBookmarkCount() {
        viewModelScope.launch {
            profileRepository.getBookmarkArtworkList(page = 0, size = 1)
                .onSuccess { result ->
                    _uiState.update {
                        it.copy(bookmarkCount = result.pagination.totalElements.toInt())
                    }
                }
                .onFailure { error ->
                    // 북마크 개수 조회 실패는 무시 (프로필은 정상 표시)
                }
        }
    }


    /** 페이징 로드 (더미 이미지 기반) */
    fun loadNextArtworks(userId: Long? = null) {
        viewModelScope.launch {
            if (_uiState.value.isLoading) return@launch

            _uiState.update { it.copy(isLoading = true) }

            val pagination = _uiState.value.pagination ?: return@launch
            if(!pagination.hasNext) return@launch

            val result = if (userId == null) {
                artRepository.getMyArtworks(page = pagination.currentPage + 1, size = pagination.pageSize)
            } else {
                artRepository.getUserArtworks(userId, page = pagination.currentPage + 1, size = pagination.pageSize)
            }

            result
                .onSuccess { artwork ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            artworks = it.artworks + artwork.artworks,
                            pagination = artwork.pagination
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.message ?: "알 수 없는 오류"
                        )
                    }
                }
        }
    }

}