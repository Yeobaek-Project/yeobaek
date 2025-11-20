package com.ssafy.yeobaeg.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.yeobaeg.domain.model.profile.Artwork
import com.ssafy.yeobaeg.domain.model.profile.BookmarkCafe
import com.ssafy.yeobaeg.domain.repository.ProfileRepository
import com.ssafy.yeobaeg.navigation.FullScreen
import com.ssafy.yeobaeg.navigation.navigator.Navigator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


data class BookmarkItemUiState(
    val isLoading: Boolean = false,
    val artwork: Artwork? = null,
    val bookmarkCafe: BookmarkCafe? = null,
    val totalBookmarkCnt: Int = 0,
    val errorMessage: String? = null
)

@HiltViewModel
class BookmarkViewModel @Inject constructor(
    private val repository: ProfileRepository,
    private val navigator: Navigator,

    ) : ViewModel() {
    private val _uiState = MutableStateFlow(BookmarkItemUiState())
    val uiState: StateFlow<BookmarkItemUiState> = _uiState.asStateFlow()

    private var currentArtworkPage = 0
    private var currentCafePage = 0

    init {
        loadBookmarkArtworks()
    }

    // ---------------------- 작품 북마크 ----------------------
    fun loadBookmarkArtworks(page: Int = 0, size: Int = 20, append: Boolean = false) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            repository.getBookmarkArtworkList(page, size)
                .onSuccess { result ->
                    _uiState.update { current ->
                        val updatedList =
                            if (append && current.artwork != null)
                                current.artwork.artworks + result.artworks
                            else
                                result.artworks

                        currentArtworkPage = result.pagination.currentPage

                        current.copy(
                            isLoading = false,
                            artwork = result.copy(artworks = updatedList),
                            errorMessage = null
                        )
                    }
                }
                .onFailure { e ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = e.message ?: "작품 북마크 목록을 불러오지 못했습니다."
                        )
                    }
                }
        }
    }

    fun loadNextArtworkPage() {
        val nextPage = currentArtworkPage + 1
        val hasNext = _uiState.value.artwork?.pagination?.hasNext ?: false
        if (hasNext && !_uiState.value.isLoading) {
            loadBookmarkArtworks(page = nextPage, append = true)
        }
    }

    // ---------------------- 카페 북마크 ----------------------
    fun loadBookmarkCafes(page: Int = 0, size: Int = 20, append: Boolean = false) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            repository.getBookmarkCafeList(page, size)
                .onSuccess { result ->
                    _uiState.update { current ->
                        val updatedList =
                            if (append && current.bookmarkCafe != null)
                                current.bookmarkCafe.bookmarkCafes + result.bookmarkCafes
                            else
                                result.bookmarkCafes

                        currentCafePage = result.pagination.currentPage

                        current.copy(
                            isLoading = false,
                            bookmarkCafe = result.copy(bookmarkCafes = updatedList),
                            errorMessage = null
                        )
                    }
                }
                .onFailure { e ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = e.message ?: "카페 북마크 목록을 불러오지 못했습니다."
                        )
                    }
                }
        }
    }

    fun loadNextCafePage() {
        val nextPage = currentCafePage + 1
        val hasNext = _uiState.value.bookmarkCafe?.pagination?.hasNext ?: false
        if (hasNext && !_uiState.value.isLoading) {
            loadBookmarkCafes(page = nextPage, append = true)
        }
    }

    fun navigateToCafeDetail(cafeId: Long) = viewModelScope.launch {
        navigator.navigate(FullScreen.CafeDetail(cafeId))
    }

    fun navigateToArtworkDetail(artworkId: Long) = viewModelScope.launch {
        navigator.navigate(FullScreen.ArtworkDetail(artworkId))
    }

    fun navigateBack() = viewModelScope.launch {
        navigator.navigateBack()
    }
}

