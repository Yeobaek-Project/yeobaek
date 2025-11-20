package com.ssafy.yeobaeg.domain.model

/**
 * 인증 상태 변화 이벤트
 *
 * 토큰 만료, 로그아웃 등 인증 상태 변화에 따른 네비게이션 처리
 */
sealed class AuthEvent {
    /**
     * 토큰이 만료되어 로그아웃 처리가 필요한 경우
     */
    data object TokenExpired : AuthEvent()

    /**
     * 사용자가 로그아웃한 경우
     */
    data object LoggedOut : AuthEvent()
}

