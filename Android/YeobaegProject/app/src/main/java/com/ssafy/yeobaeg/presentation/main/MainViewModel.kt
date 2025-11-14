package com.ssafy.yeobaeg.presentation.main

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.yeobaeg.domain.model.AuthEvent
import com.ssafy.yeobaeg.domain.model.UserRole
import com.ssafy.yeobaeg.domain.repository.ChatRepository
import com.ssafy.yeobaeg.domain.repository.ChatWebSocketRepository
import com.ssafy.yeobaeg.domain.session.AuthSessionManager
import com.ssafy.yeobaeg.presentation.common.util.SnackBarController
import com.ssafy.yeobaeg.navigation.BottomNavScreen
import com.ssafy.yeobaeg.navigation.FullScreen
import com.ssafy.yeobaeg.navigation.navigator.Navigator
import com.ssafy.yeobaeg.presentation.common.util.SnackbarMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class MainViewModel @Inject constructor(
    private val navigator: Navigator,
    private val authSessionManager: AuthSessionManager,
    private val snackbarController: SnackBarController,
    private val chatWebSocketRepository: ChatWebSocketRepository
) : ViewModel() {

    val userRole: StateFlow<UserRole?> = authSessionManager.currentUserRole

    private val _snackbarMessage = MutableStateFlow<SnackbarMessage?>(null)
    val snackbarMessage: StateFlow<SnackbarMessage?> = _snackbarMessage.asStateFlow()

    init {
        // SnackbarController 이벤트 구독
        viewModelScope.launch {
            snackbarController.snackbarEvents.collect { event ->
                _snackbarMessage.update { event }
            }
        }

        // 인증 상태 변화 이벤트 구독 (토큰 만료, 로그아웃 시 네비게이션)
        viewModelScope.launch {
            authSessionManager.authEvents.collect { event ->
                when (event) {
                    AuthEvent.TokenExpired, AuthEvent.LoggedOut -> {
                        // 토큰이 만료되거나 로그아웃하면 로그인 화면으로 이동
                        navigator.navigateAndClearBackStack(FullScreen.Login)
                    }
                }
            }
        }

        viewModelScope.launch {
            authSessionManager.currentUserId.collect { userId ->
                if (userId != null) {
                    // 로그인 성공 시 웹소켓 연결
                    try {
                        chatWebSocketRepository.connect()
                        Log.d("MainViewModel", "🌐 WebSocket connected for user $userId")
                    } catch (e: Exception) {
                        Log.e("MainViewModel", "❌ WebSocket connection failed: ${e.message}")
                    }
                } else {
                    // 로그아웃 시 연결 해제
                    chatWebSocketRepository.disconnectWebSocket()
                    Log.d("YeobaegApplication", "🔌 WebSocket disconnected after logout")
                }
            }
        }
    }

    fun clearSnackbarMessage() {
        _snackbarMessage.update { null }
    }
    fun navigateHome() = viewModelScope.launch {
        navigator.navigateAndClearBackStack(route = BottomNavScreen.Home)
    }

    fun navigateCafe() = viewModelScope.launch {
        navigator.navigateAndClearBackStack(
            route = BottomNavScreen.Cafe
        )
    }

    fun navigateArtwork() = viewModelScope.launch {
        navigator.navigateAndClearBackStack(
            route = BottomNavScreen.Artwork
        )
    }

    fun navigateChat() = viewModelScope.launch {
        navigator.navigateAndClearBackStack(
            route = BottomNavScreen.Chat
        )
    }

    fun navigateProfile() = viewModelScope.launch {
        navigator.navigateAndClearBackStack(
            route = BottomNavScreen.Profile(null)
        )
    }
}
