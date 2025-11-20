package com.ssafy.yeobaek.auth.service;

public interface RefreshTokenService {

    /**
     * Refresh Token 저장
     * @param userId 사용자 ID
     * @param refreshToken Refresh Token 값
     */
    void saveRefreshToken(Long userId, String refreshToken);

    /**
     * Refresh Token 삭제
     * @param userId 사용자 ID
     */
    void deleteRefreshToken(Long userId);

    /**
     * Refresh Token 검증
     * @param userId 사용자 ID
     * @param refreshToken 검증할 Refresh Token
     * @return 일치하면 true, 아니면 false
     */
    boolean validateRefreshToken(Long userId, String refreshToken);
}
