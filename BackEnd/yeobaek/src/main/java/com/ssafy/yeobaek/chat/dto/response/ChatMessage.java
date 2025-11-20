package com.ssafy.yeobaek.chat.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.ssafy.yeobaek.common.enums.MessageType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@Schema(description = "메시지 정보")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ChatMessage {

    @Schema(description = "메시지 ID", example = "1")
    private Long messageId;

    @Schema(description = "발신자 ID", example = "1")
    private Long senderId;

    @Schema(description = "메시지 유형", example = "TEXT")
    private MessageType messageType;

    @Schema(description = "메시지 내용", example = "안녕하세요")
    private String content;

    @Schema(description = "첨부 이미지 url", example = "https://example.com/image1.jpg")
    private String imageUrl;

    @Schema(description = "전시 ID", example = "1")
    private Long exhibitionId;

    @Schema(description = "전시 정보")
    private ExhibitionInfo exhibition;

    @Schema(description = "읽음 여부", example = "false")
    private Boolean isRead;

    @Schema(description = "메시지 전송 일시", example = "2025-11-08 10:56:52")
    private LocalDateTime createdAt;
}
