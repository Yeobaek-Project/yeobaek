// ArtistHomeViewModel.kt
package com.ssafy.yeobaeg.presentation.home.artist

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.yeobaeg.domain.model.artist.ArtHome
import com.ssafy.yeobaeg.domain.repository.ArtRepository
import com.ssafy.yeobaeg.navigation.FullScreen
import com.ssafy.yeobaeg.navigation.navigator.Navigator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale
import javax.inject.Inject

@Immutable
data class ArtistHomeUiState(
    val isLoading: Boolean = false,
    val artHome: ArtHome? = null,
    val errorMessage: String? = null
)

@HiltViewModel
class ArtistHomeViewModel @Inject constructor(
    private val artRepository: ArtRepository,
    private val navigator: Navigator,
) : ViewModel() {

    private var _uiState = MutableStateFlow(ArtistHomeUiState())
    val uiState = _uiState.asStateFlow()

    fun loadArtistData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            artRepository.getArtHome()
                .onSuccess { artHome ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            artHome = artHome,
                            errorMessage = null
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.message
                        )
                    }
                }
        }
    }

    fun navigateToRegisterArtwork() = viewModelScope.launch {
        navigator.navigate(FullScreen.RegisterArtwork)
    }

    fun navigateToMatching() = viewModelScope.launch {
        navigator.navigate(FullScreen.ArtistHomeMatching)
    }

    fun navigateToArtworkDetail(artworkId: Int) = viewModelScope.launch {
        navigator.navigate(FullScreen.ArtworkDetail(artworkId.toLong()))
    }
}

// 확장 함수: 가격 포맷팅
fun Int.toWonString(): String {
    val formatter = NumberFormat.getNumberInstance(Locale.KOREA)
    return "₩${formatter.format(this)}"
}
