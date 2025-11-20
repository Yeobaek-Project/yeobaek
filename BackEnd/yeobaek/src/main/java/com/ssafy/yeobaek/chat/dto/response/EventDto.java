package com.ssafy.yeobaek.chat.dto.response;

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
public class EventDto {

    @Schema(description = "이벤트 유형", example = "NEW_MESSAGE_RECEIVED")
    private EventType eventType;
}
