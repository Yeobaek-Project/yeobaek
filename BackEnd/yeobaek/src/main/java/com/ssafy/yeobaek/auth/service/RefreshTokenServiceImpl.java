package com.ssafy.yeobaek.auth.service;

import com.github.benmanes.caffeine.cache.Cache;
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
    private final Cache<String, String> refreshTokenCache;

    @Value("${jwt.refresh-token-expiration}")
    private long refreshTokenExpiration;

    private static final String REFRESH_TOKEN_PREFIX = "refresh_token:";

    /**
     * Refresh Token 저장
     * Redis에 저장을 시도하고, 실패 시 로컬 Caffeine 캐시에 저장
     * @param userId 사용자 ID
     * @param refreshToken Refresh Token 값
     */
    @Override
    public void saveRefreshToken(Long userId, String refreshToken) {
        log.info("Refresh Token 저장 시작 - 사용자 ID: {}", userId);
        String key = REFRESH_TOKEN_PREFIX + userId;

        try {
            // Redis에 저장 시도
            redisTemplate.opsForValue().set(
                    key,
                    refreshToken,
                    refreshTokenExpiration,
                    TimeUnit.MILLISECONDS
            );
            log.info("Refresh Token Redis 저장 완료 - 사용자 ID: {}", userId);
        } catch (Exception e) {
            // Redis 저장 실패 시 로컬 캐시로 폴백
            log.warn("Redis 저장 실패, 로컬 캐시로 폴백 - 사용자 ID: {}, 에러: {}", userId, e.getMessage());
            refreshTokenCache.put(key, refreshToken);
            log.info("Refresh Token 로컬 캐시 저장 완료 - 사용자 ID: {}", userId);
        }
    }

    /**
     * Refresh Token 삭제
     * Redis와 로컬 캐시 양쪽에서 모두 삭제 시도 (best effort)
     * @param userId 사용자 ID
     */
    @Override
    public void deleteRefreshToken(Long userId) {
        log.info("Refresh Token 삭제 시작 - 사용자 ID: {}", userId);
        String key = REFRESH_TOKEN_PREFIX + userId;

        // Redis에서 삭제 시도
        try {
            redisTemplate.delete(key);
            log.info("Refresh Token Redis 삭제 완료 - 사용자 ID: {}", userId);
        } catch (Exception e) {
            log.warn("Redis 삭제 실패 - 사용자 ID: {}, 에러: {}", userId, e.getMessage());
        }

        // 로컬 캐시에서도 삭제 (있을 수 있으므로)
        try {
            refreshTokenCache.invalidate(key);
            log.debug("Refresh Token 로컬 캐시 삭제 완료 - 사용자 ID: {}", userId);
        } catch (Exception e) {
            log.warn("로컬 캐시 삭제 실패 - 사용자 ID: {}, 에러: {}", userId, e.getMessage());
        }

        log.info("Refresh Token 삭제 완료 - 사용자 ID: {}", userId);
    }

    /**
     * Refresh Token 검증
     * Redis에서 조회를 시도하고, 실패 시 로컬 캐시에서 조회
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
     * Redis에서 조회를 시도하고, 실패 시 로컬 Caffeine 캐시에서 조회
     * @param userId 사용자 ID
     * @return Refresh Token 값 (없으면 null)
     */
    private String getRefreshToken(Long userId) {
        String key = REFRESH_TOKEN_PREFIX + userId;

        try {
            // Redis에서 조회 시도
            String token = redisTemplate.opsForValue().get(key);
            log.debug("Redis에서 Refresh Token 조회 완료 - 사용자 ID: {}", userId);
            return token;
        } catch (Exception e) {
            // Redis 조회 실패 시 로컬 캐시로 폴백
            log.warn("Redis 조회 실패, 로컬 캐시로 폴백 - 사용자 ID: {}, 에러: {}", userId, e.getMessage());
            String token = refreshTokenCache.getIfPresent(key);
            log.debug("로컬 캐시에서 Refresh Token 조회 완료 - 사용자 ID: {}, 존재 여부: {}", userId, token != null);
            return token;
        }
    }
}
