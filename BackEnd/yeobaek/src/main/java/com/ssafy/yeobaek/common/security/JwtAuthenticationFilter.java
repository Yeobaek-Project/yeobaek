package com.ssafy.yeobaek.common.security;

import com.ssafy.yeobaek.common.code.ResponseCode;
import com.ssafy.yeobaek.common.enums.UserRole;
import com.ssafy.yeobaek.common.util.JwtUtil;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

/**
 * JWT 인증 필터
 * 모든 요청에서 JWT 토큰을 검증하고 SecurityContext에 인증 정보 설정
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        try {
            // Authorization 헤더에서 JWT 토큰 추출
            String token = extractTokenFromRequest(request);

            // 토큰이 없는 경우
            if (token == null) {
                request.setAttribute("exception", ResponseCode.TOKEN_MISSING);
            } else {
                // 토큰에서 사용자 ID와 UserType 추출
                Long userId = jwtUtil.getUserIdFromToken(token);
                UserRole userType = jwtUtil.getUserTypeFromToken(token);

                // JwtUserPrincipal 생성
                JwtUserPrincipal principal = new JwtUserPrincipal(userId, userType);

                // SecurityContext에 인증 정보 설정
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                principal,
                                null,
                                Collections.singletonList(
                                        new SimpleGrantedAuthority("ROLE_" + userType.name())
                                )
                        );
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (ExpiredJwtException e) {
            // 토큰 만료
            logger.error("JWT 토큰 만료: " + e.getMessage());
            request.setAttribute("exception", ResponseCode.EXPIRED_TOKEN);
        } catch (JwtException e) {
            // 토큰 형식 오류, 서명 오류 등
            logger.error("JWT 토큰 유효하지 않음: " + e.getMessage());
            request.setAttribute("exception", ResponseCode.INVALID_TOKEN);
        } catch (Exception e) {
            // 기타 예외
            logger.error("JWT 인증 실패: " + e.getMessage());
            request.setAttribute("exception", ResponseCode.UNAUTHORIZED);
        }

        // 다음 필터로 진행
        filterChain.doFilter(request, response);
    }

    /**
     * Authorization 헤더에서 Bearer 토큰 추출
     * @param request HTTP 요청
     * @return JWT 토큰 (Bearer 제외)
     */
    private String extractTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
