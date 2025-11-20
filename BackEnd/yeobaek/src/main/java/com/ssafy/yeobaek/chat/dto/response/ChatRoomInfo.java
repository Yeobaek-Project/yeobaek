package com.ssafy.yeobaek.chat.dto.response;

import com.ssafy.yeobaek.common.enums.MessageType;
import com.ssafy.yeobaek.common.enums.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@Schema(description = "채팅방 기본 정보")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatRoomInfo {

    @Schema(description = "채팅방 ID", example = "1")
    private Long chatRoomId;

    @Schema(description = "상대방 ID", example = "1")
    private Long partnerId;

    @Schema(description = "상대방 이름 (작가명 또는 카페명)", example = "홍길동")
    private String partnerName;

    @Schema(description = "상대방 프로필 이미지 url", example = "https://example.com/image1.jpg")
    private String partnerImageUrl;

    @Schema(description = "상대방 역할", example = "ARTIST")
    private UserRole partnerRole;

    @Schema(description = "읽지 않은 메시지 수", example = "3")
    private Long unreadCount;

    @Schema(description = "마지막 메시지 내용", example = "안녕하세요")
    private String lastMessage;

    @Schema(description = "마지막 메시지 유형", example = "TEXT")
    private MessageType lastMessageType;

    @Schema(description = "마지막 메시지 일시", example = "2025-11-06 02:07:41")
    private LocalDateTime lastMessageAt;
}
