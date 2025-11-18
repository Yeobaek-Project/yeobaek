package com.ssafy.yeobaeg.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.yeobaeg.domain.model.art.PopularArtwork
import com.ssafy.yeobaeg.domain.model.cafe.CafeSummary
import com.ssafy.yeobaeg.domain.repository.ArtRepository
import com.ssafy.yeobaeg.domain.repository.CafeRepository
import com.ssafy.yeobaeg.navigation.FullScreen
import com.ssafy.yeobaeg.navigation.navigator.Navigator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class GeneralHomeUiState(
    val popularCafes: List<CafeSummary> = emptyList(),
    val popularArtworks: List<PopularArtwork> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class GeneralHomeViewModel @Inject constructor(
    private val cafeRepository: CafeRepository,
    private val artRepository: ArtRepository,
    private val navigator: Navigator
) : ViewModel() {

    private val _uiState = MutableStateFlow(GeneralHomeUiState())
    val uiState: StateFlow<GeneralHomeUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            val cafesResult = cafeRepository.getPopularCafes(15)
            val artworksResult = artRepository.getPopularArtworks(15)

            cafesResult.onSuccess { cafes ->
                _uiState.update { it.copy(popularCafes = cafes) }
            }.onFailure { e ->
                _uiState.update { it.copy(error = e.message) }
            }

            artworksResult.onSuccess { artworks ->
                _uiState.update { it.copy(popularArtworks = artworks) }
            }.onFailure { e ->
                _uiState.update { it.copy(error = e.message) }
            }

            _uiState.update { it.copy(isLoading = false) }
        }
    }

    fun navigateToCafeDetail(cafeId: Long) = viewModelScope.launch {
        // TODO: 카페 상세 페이지 추가 시 구현
         navigator.navigate(FullScreen.CafeDetail(cafeId))
    }

    fun navigateToArtworkDetail(artworkId: Long) = viewModelScope.launch {
        navigator.navigate(FullScreen.ArtworkDetail(artworkId))
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}

