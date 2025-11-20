package com.ssafy.yeobaek.common.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssafy.yeobaek.common.code.ResponseCode;
import com.ssafy.yeobaek.common.dto.ResponseDto;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 인증 실패 시 처리 핸들러
 * 인증되지 않은 사용자가 보호된 리소스에 접근 시 401 응답
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException
    ) throws IOException, ServletException {
        // request attribute에서 예외 정보 가져오기
        ResponseCode responseCode = (ResponseCode) request.getAttribute("exception");

        // 예외 정보가 없으면 기본 UNAUTHORIZED 사용
        if (responseCode == null) {
            responseCode = ResponseCode.UNAUTHORIZED;
        }

        // 401 Unauthorized 응답
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        // ResponseDto 형식으로 응답
        ResponseDto<Void> errorResponse = ResponseDto.error(responseCode);
        String jsonResponse = objectMapper.writeValueAsString(errorResponse);

        response.getWriter().write(jsonResponse);
    }
}
