package com.ssafy.yeobaek.chat.listener;

import com.ssafy.yeobaek.chat.service.ChatPresenceService;
import com.ssafy.yeobaek.common.security.JwtUserPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

import java.security.Principal;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Component
@RequiredArgsConstructor
public class StompEventListener {

    private final ChatPresenceService chatPresenceService;
    private static final Pattern chatRoomPattern = Pattern.compile("/sub/chat/room/(\\d+)");
    private static final String PRESENCE_INFO_KEY = "presenceInfo";

    /**
     * 사용자가 STOMP 채널을 구독할 때 호출되는 이벤트 리스너
     * 채팅방 구독 시, 사용자를 해당 채팅방의 접속자 목록에 추가한다.
     * @param event 구독 이벤트
     */
    @EventListener
    public void handleSessionSubscribe(SessionSubscribeEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        String destination = accessor.getDestination();

        if (destination == null) {
            return;
        }

        Matcher matcher = chatRoomPattern.matcher(destination);
        // 채팅방 구독 주소인지 확인
        if (matcher.matches()) {
            try {
                Long roomId = Long.parseLong(matcher.group(1));

                Principal principalFromEvent = event.getUser();
                Authentication authentication = null;
                
                if (principalFromEvent instanceof Authentication) {
                    authentication = (Authentication) principalFromEvent;
                } else if (accessor.getSessionAttributes() != null) {
                    authentication = (Authentication) accessor.getSessionAttributes().get("userAuthentication");
                }
                
                if (authentication == null) {
                    log.warn("Cannot track presence for non-authenticated user on subscribe. Authentication object not found in event or session.");
                    return;
                }
                                
                if (!(authentication.getPrincipal() instanceof JwtUserPrincipal)) {
                    log.warn("Cannot track presence for non-authenticated user on subscribe. Authentication's principal is not JwtUserPrincipal.");
                    return;
                }
                
                JwtUserPrincipal jwtPrincipal = (JwtUserPrincipal) authentication.getPrincipal();
                Long userId = jwtPrincipal.userId();

                // 접속자 목록에 추가
                chatPresenceService.userJoined(roomId, userId);

                // 세션 속성에 접속 정보를 저장 (연결 종료 시 사용)
                Map<String, Object> sessionAttributes = accessor.getSessionAttributes();
                if (sessionAttributes != null) {
                    sessionAttributes.put(PRESENCE_INFO_KEY, new PresenceInfo(userId, roomId));
                    log.debug("Stored presence info in session {} for user {} in room {}", accessor.getSessionId(), userId, roomId);
                }

            } catch (NumberFormatException e) {
                log.warn("Could not parse roomId from destination: {}", destination);
            }
        }
    }

    /**
     * 사용자의 웹소켓 연결이 끊어졌을 때 호출되는 이벤트 리스너
     * 세션에 저장된 정보를 바탕으로, 사용자를 접속자 목록에서 제거한다.
     * @param event 연결 종료 이벤트
     */
    @EventListener
    public void handleSessionDisconnect(SessionDisconnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        Map<String, Object> sessionAttributes = accessor.getSessionAttributes();

        if (sessionAttributes != null && sessionAttributes.containsKey(PRESENCE_INFO_KEY)) {
            PresenceInfo presenceInfo = (PresenceInfo) sessionAttributes.get(PRESENCE_INFO_KEY);
            if (presenceInfo != null) {
                chatPresenceService.userLeft(presenceInfo.roomId(), presenceInfo.userId());
                log.info("User {} disconnected from room {}.", presenceInfo.userId(), presenceInfo.roomId());
            }
        } else {
            log.debug("No presence info found for disconnected session {}", accessor.getSessionId());
        }
    }

    /**
     * 세션에 저장될 사용자-채팅방 접속 정보를 담는 record
     */
    private record PresenceInfo(Long userId, Long roomId) {}
}
