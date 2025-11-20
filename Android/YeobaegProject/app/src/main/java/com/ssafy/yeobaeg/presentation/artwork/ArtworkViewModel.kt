// ArtExploreViewModel.kt
package com.ssafy.yeobaeg.presentation.artwork

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.yeobaeg.domain.model.ArtworkCategory
import com.ssafy.yeobaeg.domain.repository.ArtRepository
import com.ssafy.yeobaeg.domain.repository.RecentSearchRepository
import com.ssafy.yeobaeg.navigation.FullScreen
import com.ssafy.yeobaeg.navigation.navigator.Navigator
import com.ssafy.yeobaeg.presentation.common.util.SnackBarController
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ArtExploreViewModel @Inject constructor(
    private val recentSearchRepository: RecentSearchRepository,
    private val artRepository: ArtRepository,
    private val navigator: Navigator,
    private val snackBarController: SnackBarController
) : ViewModel() {
    private val _uiState = MutableStateFlow(ArtExploreState())
    val uiState: StateFlow<ArtExploreState> = _uiState.asStateFlow()

    init {
        loadArtworks()

        viewModelScope.launch {
            recentSearchRepository.getRecentKeywords().collect { recents ->
                update { copy(recent = recents) }
            }
        }
    }

    private fun loadArtworks() {
        val category = _uiState.value.selectedCategory

        viewModelScope.launch {
            update { copy(isSearchLoading = true) }

            artRepository.getArtworks(
                category = if(category== ArtworkCategory.ALL) null else listOf(category.name),
                maxWidth = _uiState.value.maxWidthCm,
                maxHeight = _uiState.value.maxHeightCm,
                page = (_uiState.value.paginationInfo?.currentPage?.plus(1)) ?: 0,
                size = _uiState.value.paginationInfo?.pageSize ?: 20,
            )
                .onSuccess { response ->
                    update {
                        copy(
                            artworks = artworks + response.artworkSummaries,
                            paginationInfo = response.paginationInfo,
                            isSearchLoading = false
                        )
                    }
                }
                .onFailure { error ->
                    Log.d("ArtExploreViewModel", "loadArtworks error: ${error.message}")
                    snackBarController.showError(error.message ?: "오류")
                    update { copy(isSearchLoading = false) }
                }
        }
    }

    fun loadNextPage() {
        val currentMode = _uiState.value.mode
        val hasNext = _uiState.value.paginationInfo?.hasNext ?: false

        if (_uiState.value.isSearchLoading || !hasNext) return

        when (currentMode) {
            SearchMode.BROWSE -> loadArtworks()
            SearchMode.RESULT -> search()
            SearchMode.RECENT -> {} // 최근 검색어는 페이지네이션 없음
        }
    }

    fun selectTabSearch() = update {
        copy(
            mode = SearchMode.RECENT,
            paginationInfo = null // 페이지네이션 초기화
        )
    }

    fun selectTabBrowse() = update {
        copy(
            mode = SearchMode.BROWSE,
            paginationInfo = null // 페이지네이션 초기화
        )
    }

    fun setQuery(q: String) = update { copy(query = q) }

    fun setSizeFilter(maxWidth: Int?, maxHeight: Int?) {
        update {
            copy(maxWidthCm = maxWidth, maxHeightCm = maxHeight)
        }

        if (_uiState.value.mode == SearchMode.RESULT) {
            search() // 필터 변경 시 첫 페이지부터 다시 검색
        }
        else if(_uiState.value.mode == SearchMode.BROWSE) {
            loadArtworks()
        }
    }

    fun selectCategory(cat: ArtworkCategory) {
        update {
            copy(
                artworks = emptyList(),
                selectedCategory = cat,
                paginationInfo = null // 카테고리 변경 시 페이지네이션 초기화
            )
        }
        loadArtworks()
    }

    fun search() = viewModelScope.launch {
        val q = _uiState.value.query.trim()
        if (q.isEmpty()) return@launch

        recentSearchRepository.add(q)

        if (_uiState.value.mode != SearchMode.RESULT) {
            update {
                copy(
                    mode = SearchMode.RESULT,
                    isSearchLoading = true,
                    paginationInfo = null // 새 검색 시 페이지네이션 초기화
                )
            }
        } else {
            update { copy(isSearchLoading = true) }
        }

        val result = artRepository.searchArt(
            keyword = q,
            minWidth = _uiState.value.minWidthCm,
            maxWidth = _uiState.value.maxWidthCm,
            minHeight = _uiState.value.minWidthCm,
            maxHeight = _uiState.value.maxHeightCm,
            page = _uiState.value.paginationInfo?.currentPage?.plus(1) ?: 0,
            size = 20
        )

        result.onSuccess { searchResult ->
            update {
                copy(
                    resultArtists = searchResult.artistSummaries,
                    resultArtworks = resultArtworks + searchResult.artworkSummaries,
                    paginationInfo = searchResult.paginationInfo,
                    isSearchLoading = false
                )
            }
        }.onFailure { error ->
            Log.d("ArtExploreViewModel", "search error: ${error.message}")
            update { copy(isSearchLoading = false) }
            snackBarController.showError(error.message ?: "오류")
        }
    }

    fun clearRecent() = viewModelScope.launch { recentSearchRepository.clear() }
    fun removeRecent(item: String) = viewModelScope.launch { recentSearchRepository.remove(item) }

    fun navigateToDetail(artWorkId: Long) = viewModelScope.launch {
        navigator.navigate(FullScreen.ArtworkDetail(artWorkId))
    }

    fun navigateToArtistDetail(artistId: Long) = viewModelScope.launch {
        navigator.navigate(FullScreen.ArtistProfile(artistId))
    }

    private inline fun update(block: ArtExploreState.() -> ArtExploreState) {
        _uiState.update(block)
    }
}