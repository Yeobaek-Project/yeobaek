package com.ssafy.yeobaek.common.security;

import com.ssafy.yeobaek.common.enums.UserRole;

/**
 * JWT 인증 정보를 담는 Principal
 * @param userId 사용자 ID (PK)
 * @param userType 사용자 유형 (ARTIST, CAFE_OWNER, GENERAL)
 */
public record JwtUserPrincipal(
        Long userId,
        UserRole userType
) {
}
