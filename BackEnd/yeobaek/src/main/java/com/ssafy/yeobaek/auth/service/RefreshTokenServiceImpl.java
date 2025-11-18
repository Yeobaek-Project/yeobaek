package com.ssafy.yeobaek.auth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private final StringRedisTemplate redisTemplate;

    @Value("${jwt.refresh-token-expiration}")
    private long refreshTokenExpiration;

    private static final String REFRESH_TOKEN_PREFIX = "refresh_token:";

    /**
     * Refresh Token 저장
     * @param userId 사용자 ID
     * @param refreshToken Refresh Token 값
     */
    @Override
    public void saveRefreshToken(Long userId, String refreshToken) {
        log.info("Refresh Token 저장 시작 - 사용자 ID: {}", userId);
        String key = REFRESH_TOKEN_PREFIX + userId;
        redisTemplate.opsForValue().set(
                key,
                refreshToken,
                refreshTokenExpiration,
                TimeUnit.MILLISECONDS
        );
        log.info("Refresh Token 저장 완료 - 사용자 ID: {}", userId);
    }

    /**
     * Refresh Token 삭제
     * @param userId 사용자 ID
     */
    @Override
    public void deleteRefreshToken(Long userId) {
        log.info("Refresh Token 삭제 시작 - 사용자 ID: {}", userId);
        String key = REFRESH_TOKEN_PREFIX + userId;
        redisTemplate.delete(key);
        log.info("Refresh Token 삭제 완료 - 사용자 ID: {}", userId);
    }

    /**
     * Refresh Token 검증
     * @param userId 사용자 ID
     * @param refreshToken 검증할 Refresh Token
     * @return 일치하면 true, 아니면 false
     */
    @Override
    public boolean validateRefreshToken(Long userId, String refreshToken) {
        log.info("Refresh Token 검증 시작 - 사용자 ID: {}", userId);
        String storedToken = getRefreshToken(userId);
        boolean isValid = storedToken != null && storedToken.equals(refreshToken);
        if (!isValid) {
            log.warn("Refresh Token 검증 실패 - 사용자 ID: {}, 저장된 토큰 존재 여부: {}", userId, storedToken != null);
        }
        log.info("Refresh Token 검증 완료 - 사용자 ID: {}, 결과: {}", userId, isValid);
        return isValid;
    }

    /**
     * Refresh Token 조회
     * @param userId 사용자 ID
     * @return Refresh Token 값 (없으면 null)
     */
    private String getRefreshToken(Long userId) {
        log.debug("Redis에서 Refresh Token 조회 - 사용자 ID: {}", userId);
        String key = REFRESH_TOKEN_PREFIX + userId;
        return redisTemplate.opsForValue().get(key);
    }
}
