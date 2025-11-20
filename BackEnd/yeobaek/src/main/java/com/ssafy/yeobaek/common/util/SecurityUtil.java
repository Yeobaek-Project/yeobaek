package com.ssafy.yeobaek.common.util;

import com.ssafy.yeobaek.common.code.ResponseCode;
import com.ssafy.yeobaek.common.enums.UserRole;
import com.ssafy.yeobaek.common.exception.CustomException;
import com.ssafy.yeobaek.common.security.JwtUserPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Spring Security 인증 정보 접근 유틸리티
 */
public class SecurityUtil {

    /**
     * 현재 인증된 사용자의 ID 반환
     * @return 사용자 ID
     * @throws CustomException 인증되지 않은 경우
     */
    public static Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof JwtUserPrincipal principal) {
            return principal.userId();
        }
        throw new CustomException(ResponseCode.UNAUTHORIZED);
    }

    /**
     * 현재 인증된 사용자의 유형 반환
     * @return 사용자 유형 (UserRole)
     * @throws CustomException 인증되지 않은 경우
     */
    public static UserRole getCurrentUserType() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof JwtUserPrincipal principal) {
            return principal.userType();
        }
        throw new CustomException(ResponseCode.UNAUTHORIZED);
    }

    /**
     * 현재 사용자가 특정 UserRole인지 확인
     * @param userType 확인할 UserRole
     * @return 일치하면 true
     */
    public static boolean isCurrentUserType(UserRole userType) {
        try {
            return getCurrentUserType() == userType;
        } catch (CustomException e) {
            return false;
        }
    }
}
