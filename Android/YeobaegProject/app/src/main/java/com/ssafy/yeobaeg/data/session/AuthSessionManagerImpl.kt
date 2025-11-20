package com.ssafy.yeobaeg.data.session

import android.util.Log
import com.ssafy.yeobaeg.data.datasource.local.AuthDataSource
import com.ssafy.yeobaeg.domain.session.AuthSessionManager
import com.ssafy.yeobaeg.domain.model.AuthEvent
import com.ssafy.yeobaeg.domain.model.UserRole
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class

AuthSessionManagerImpl @Inject constructor(
    private val authDataSource: AuthDataSource
) : AuthSessionManager {
    
    private val _currentUserRole = MutableStateFlow<UserRole?>(null)
    override val currentUserRole: StateFlow<UserRole?> = _currentUserRole.asStateFlow()

    private val _currentUserId = MutableStateFlow<Long?>(null)
    override val currentUserId: StateFlow<Long?> = _currentUserId.asStateFlow()
    
    private val _authEvents = MutableSharedFlow<AuthEvent>()
    override val authEvents: SharedFlow<AuthEvent> = _authEvents.asSharedFlow()
    
    override suspend fun isLoggedIn(): Boolean {
        val token = authDataSource.getAccessToken()
        return token != null
    }
    
    override suspend fun updateUserRole(role: UserRole?) {
        _currentUserRole.update {
            role
        }
        Log.d("AuthSessionManager", "User role updated to: $role")
    }

    override suspend fun updateUserId(userId: Long?) {
        _currentUserId.update {
            userId
        }
        Log.d("AuthSessionManager", "User ID updated to: $userId")
    }

    override suspend fun updateUserInfo(role: UserRole?, userId: Long?) {
        _currentUserRole.value = role
        _currentUserId.value = userId
        Log.d("AuthSessionManager", "User info updated - role: $role, userId: $userId")
    }
    
    override suspend fun clearUserState() {
        _currentUserRole.value = null
        _currentUserId.value = null
    }
    
    override suspend fun logout() {
        // 유저 롤 및 ID 초기화
        _currentUserRole.value = null
        _currentUserId.value = null
        // 토큰 삭제
        authDataSource.clearTokens()
        _authEvents.emit(AuthEvent.LoggedOut)
        Log.d("AuthSessionManager", "User logged out - role, userId and tokens cleared")
    }
    
    override suspend fun emitTokenExpired() {
        _authEvents.emit(AuthEvent.TokenExpired)
    }
}
