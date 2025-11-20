package com.ssafy.yeobaeg.domain.session

import com.ssafy.yeobaeg.domain.model.AuthEvent
import com.ssafy.yeobaeg.domain.model.UserRole
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * 인증 및 세션 상태를 관리하는 Manager
 * 
 * Repository는 데이터 계층에 집중하고,
 * 비즈니스 로직(로그인 상태, 유저 롤 관리)은 SessionManager가 담당
 */
interface AuthSessionManager {
    
    /**
     * 현재 로그인한 사용자의 Role StateFlow
     */
    val currentUserRole: StateFlow<UserRole?>

    /**
     * 현재 로그인한 사용자의 ID StateFlow
     */
    val currentUserId: StateFlow<Long?>
    
    /**
     * 인증 관련 이벤트를 구독
     * (예: 토큰 만료로 인한 자동 로그아웃)
     */
    val authEvents: SharedFlow<AuthEvent>
    
    /**
     * 로그인 상태 확인
     */
    suspend fun isLoggedIn(): Boolean

    /**
     * 사용자 Role 업데이트
     */
    suspend fun updateUserRole(role: UserRole?)

    /**
     * 사용자 ID 업데이트
     */
    suspend fun updateUserId(userId: Long?)

    /**
     * 사용자 Role과 ID를 동시에 업데이트
     */
    suspend fun updateUserInfo(role: UserRole?, userId: Long?)
    
    /**
     * UserRole 상태 초기화
     */
    suspend fun clearUserState()


    /**
     * 로그아웃 처리
     * - UserRole 초기화
     * - 저장된 JWT 토큰 삭제
     */
    suspend fun logout()


    /**
     * 토큰 만료 이벤트 발행
     */
    suspend fun emitTokenExpired()
}
