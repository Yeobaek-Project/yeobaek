package com.ssafy.yeobaek.chat.dto.response;

import com.ssafy.yeobaek.common.enums.MessageType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Schema(description = "새 메시지 도착 시 채팅방 업데이트 요약 정보")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatRoomUpdateSummary {

    @Schema(description = "채팅방 ID", example = "1")
    private Long chatRoomId;

    @Schema(description = "읽지 않은 메시지 수", example = "3")
    private Long unreadCount;

    @Schema(description = "마지막 메시지 내용", example = "안녕하세요")
    private String lastMessage;

    @Schema(description = "마지막 메시지 유형", example = "TEXT")
    private MessageType lastMessageType;

    @Schema(description = "마지막 메시지 일시", example = "2025-11-06 02:07:41")
    private LocalDateTime lastMessageAt;
}
