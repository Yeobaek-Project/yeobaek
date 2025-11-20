package com.ssafy.yeobaek.chat.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Schema(description = "채팅방 응답")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoomData {

    @Schema(description = "채팅방 정보")
    private ChatRoomSummary chatRoom;
}
