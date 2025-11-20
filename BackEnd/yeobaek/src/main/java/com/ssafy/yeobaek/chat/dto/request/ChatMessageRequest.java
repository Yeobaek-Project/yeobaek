package com.ssafy.yeobaek.chat.dto.request;

import com.ssafy.yeobaek.common.enums.MessageType;
import com.ssafy.yeobaek.common.validation.ValidEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Schema(description = "메시지 전송 요청")
@Getter
@Setter
public class ChatMessageRequest {

    @NotNull(message = "MISSING_REQUIRED_FIELD")
    @Schema(description = "채팅방 id", example = "1")
    private Long chatRoomId;

    @NotNull(message = "MISSING_REQUIRED_FIELD")
    @Schema(description = "메시지 유형", example = "TEXT")
    private MessageType messageType;

    @Schema(description = "메시지 내용", example = "안녕하세요")
    private String content;

    @Schema(description = "이미지 url", example = "https://example.com/image1.jpg")
    private String imageUrl;
}
