package com.ssafy.yeobaek.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
@Component
public class LoggingInterceptor implements HandlerInterceptor {

    private static final String REQUEST_LOG_ID = "request_log_id";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestId = UUID.randomUUID().toString();
        request.setAttribute(REQUEST_LOG_ID, requestId);

        log.info("API 요청 [{}]: {} {}", requestId, request.getMethod(), request.getRequestURI());
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        String requestId = (String) request.getAttribute(REQUEST_LOG_ID);
        log.info("API 응답 [{}]: {} {}, 상태: {}", requestId, request.getMethod(), request.getRequestURI(), response.getStatus());
        if (ex != null) {
            log.error("API 처리 중 예외 발생 [{}]:", requestId, ex);
        }
    }
}
