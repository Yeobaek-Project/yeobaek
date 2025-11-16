package com.ssafy.yeobaek.common.security;

import com.ssafy.yeobaek.common.enums.UserRole;
import com.ssafy.yeobaek.common.util.JwtUtil;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Slf4j
@Component
@RequiredArgsConstructor
public class StompHandler implements ChannelInterceptor {

    private final JwtUtil jwtUtil;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        StompCommand command = accessor.getCommand();

        if (StompCommand.CONNECT.equals(command)) {
            String jwtToken = accessor.getFirstNativeHeader("Authorization");
            log.info("STOMP CONNECT - Authorization header found.");

            if (jwtToken != null && jwtToken.startsWith("Bearer ")) {
                String token = jwtToken.substring(7);
                try {
                    if (jwtUtil.validateToken(token)) {
                        Long userId = jwtUtil.getUserIdFromToken(token);
                        UserRole userType = jwtUtil.getUserTypeFromToken(token);

                        JwtUserPrincipal principal = new JwtUserPrincipal(userId, userType);
                        Authentication authentication = new UsernamePasswordAuthenticationToken(
                                principal,
                                null,
                                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + userType.name()))
                        );
                        // 세션 속성에 인증 정보 저장
                        accessor.getSessionAttributes().put("userAuthentication", authentication);
                        accessor.setUser(authentication);
                        log.info("STOMP User authenticated and session attribute set: userId={}", userId);
                    }
                } catch (Exception e) {
                    log.warn("STOMP connection failed: Invalid JWT token - {}", e.getMessage());
                    throw new IllegalArgumentException("Invalid JWT token");
                }
            } else {
                log.warn("STOMP connection failed: Missing or invalid Authorization header");
                throw new IllegalArgumentException("Missing or invalid Authorization header");
            }
        } else if (StompCommand.SEND.equals(command) || StompCommand.SUBSCRIBE.equals(command)) {
            Authentication authentication = (Authentication) accessor.getSessionAttributes().get("userAuthentication");

            // If authentication is missing, deny access.
            if (authentication == null) {
                log.error("Authentication not found in session for {} command.", command);
                throw new IllegalArgumentException("User not authenticated.");
            }
            accessor.setUser(authentication); // Ensure user is set for the current message

            // Authorize SUBSCRIBE command for notification topics
            if (StompCommand.SUBSCRIBE.equals(command)) {
                String destination = accessor.getDestination();
                if (destination != null && destination.startsWith("/sub/notifications/")) {
                    try {
                        String topicUserIdStr = destination.substring("/sub/notifications/".length());
                        Long topicUserId = Long.parseLong(topicUserIdStr);

                        JwtUserPrincipal principal = (JwtUserPrincipal) authentication.getPrincipal();
                        Long authenticatedUserId = principal.userId();

                        if (!authenticatedUserId.equals(topicUserId)) {
                            log.warn("SUBSCRIPTION DENIED: User {} attempted to subscribe to notifications for user {}.", authenticatedUserId, topicUserId);
                            throw new IllegalArgumentException("Unauthorized subscription.");
                        }
                        log.info("SUBSCRIPTION ALLOWED: User {} to topic {}.", authenticatedUserId, destination);
                    } catch (Exception e) {
                        log.error("Error during subscription authorization check for destination: {}", destination, e);
                        throw new IllegalArgumentException("Invalid subscription topic format.");
                    }
                }
            }
        }
        return message;
    }
}
