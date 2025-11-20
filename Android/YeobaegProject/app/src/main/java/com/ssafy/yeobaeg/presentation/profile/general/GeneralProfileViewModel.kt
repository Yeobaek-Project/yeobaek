package com.ssafy.yeobaeg.presentation.profile.general


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.yeobaeg.domain.model.profile.UserInfo
import com.ssafy.yeobaeg.domain.repository.ProfileRepository
import com.ssafy.yeobaeg.domain.session.AuthSessionManager
import com.ssafy.yeobaeg.navigation.navigator.Navigator
import com.ssafy.yeobaeg.presentation.profile.ProfileViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class GeneralProfileUiState(
    val isLoading: Boolean = false,
    val userInfo: UserInfo = UserInfo(),
    val errorMessage: String? = null
)

@HiltViewModel
class GeneralProfileViewModel @Inject constructor(
    private val navigator: Navigator,
    private val profileRepository: ProfileRepository, authSessionManager: AuthSessionManager

) : ProfileViewModel(navigator, authSessionManager) {
    private val _uiState = MutableStateFlow(GeneralProfileUiState())
    val uiState: StateFlow<GeneralProfileUiState> = _uiState.asStateFlow()

    fun loadGeneralProfile() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            profileRepository.getProfile()
                .onSuccess { profile ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            userInfo = profile.userInfo,
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

    fun refresh() {
        loadGeneralProfile()
    }

}