package com.ssafy.yeobaek.chat.service;

import com.ssafy.yeobaek.chat.dto.response.ChatMessage;
import com.ssafy.yeobaek.chat.dto.response.EventDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class WebSocketMessageService {

    private final SimpMessagingTemplate messagingTemplate;

    public void sendChatMessage(Long chatRoomId, ChatMessage chatMessage) {
        log.info("WebSocket으로 채팅 메시지 전송 시작 - 채팅방 ID: {}", chatRoomId);
        String destination = "/sub/chat/room/" + chatRoomId;
        messagingTemplate.convertAndSend(destination, chatMessage);
        log.info("WebSocket 메시지 전송 완료 - 목적지: {}", destination);
    }

    public void sendEventToUser(Long userId, EventDto eventDto) {
        log.info("사용자에게 이벤트 전송 시작 - 사용자 ID: {}, 이벤트 타입: {}", userId, eventDto.getEventType());
        String destination = "/sub/notifications/" + userId;
        messagingTemplate.convertAndSend(destination, eventDto);
        log.info("사용자에게 이벤트 전송 완료 - 목적지: {}", destination);
    }
}
