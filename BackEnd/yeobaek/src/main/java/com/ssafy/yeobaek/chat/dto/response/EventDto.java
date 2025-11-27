package com.ssafy.yeobaek.chat.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.ssafy.yeobaek.common.enums.EventType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EventDto {

    @Schema(description = "이벤트 유형", example = "NEW_MESSAGE_RECEIVED")
    private EventType eventType;

    @Schema(description = "새 메시지 도착 시 채팅방 업데이트 요약 정보")
    private ChatRoomUpdateSummary chatRoomUpdateSummary;
}